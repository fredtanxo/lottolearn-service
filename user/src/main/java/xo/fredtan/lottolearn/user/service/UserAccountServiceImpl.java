package xo.fredtan.lottolearn.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.user.constants.RoleConstants;
import xo.fredtan.lottolearn.api.user.constants.UserAccountType;
import xo.fredtan.lottolearn.api.user.service.UserAccountService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.Role;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.UserAccount;
import xo.fredtan.lottolearn.domain.user.UserRole;
import xo.fredtan.lottolearn.domain.user.response.UserCode;
import xo.fredtan.lottolearn.domain.user.response.UserOfAccount;
import xo.fredtan.lottolearn.user.dao.*;

import java.util.List;
import java.util.Objects;

@Service
@DubboService
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountMapper userAccountMapper;
    private final UserAccountRepository userAccountRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public QueryResponseData<UserAccount> findAllUserAccount(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserAccount> accounts = userAccountRepository.findAll(pageRequest);

        QueryResult<UserAccount> queryResult = new QueryResult<>(accounts.getTotalElements(), accounts.getContent());
        return QueryResponseData.ok(queryResult);
    }

    @Override
    public UniqueQueryResponseData<UserAccount> findUserAccountById(Long accountId) {
        UserAccount userAccount = userAccountMapper.selectUserAccountById(accountId);
        return UniqueQueryResponseData.ok(userAccount);
    }

    @Override
    public QueryResponseData<UserAccount> findCurrentAccounts() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        List<UserAccount> userAccounts = userAccountMapper.selectAllAccountsByUserId(userId);
        QueryResult<UserAccount> queryResult = new QueryResult<>((long) userAccounts.size(), userAccounts);
        return QueryResponseData.ok(queryResult);
    }

    @Override
    @Transactional
    public BasicResponseData addUserAccount(UserAccount userAccount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());
        if (!userId.equals(userAccount.getUserId())) {
            // check if it's admin
            ApiExceptionCast.unauthorized();
        }

        checkUniqueAccount(null, userAccount);

        userAccount.setId(null);
        String credential = checkAndGetCredential(userAccount);
        userAccount.setCredential(credential);
        userAccount.setStatus(true);
        return BasicResponseData.ok();
    }

    private String checkAndGetCredential(UserAccount userAccount) {
        String pass = userAccount.getCredential();
        String credential = null;
        if (UserAccountType.PASSWORD.getType().equals(userAccount.getType())) {
            if (!StringUtils.hasText(pass) || pass.length() < 8 || pass.length() > 50) {
                ApiExceptionCast.invalidParam();
            }
            credential = passwordEncoder.encode(pass);
        }
        return credential;
    }

    private void checkUniqueAccount(Long userId, UserAccount userAccount) {
        UserOfAccount userOfAccount = userAccountMapper.selectUserByAccountAndType(userAccount.getAccount(), userAccount.getType());
        if (Objects.nonNull(userOfAccount)) {
            if (Objects.isNull(userId) || !userId.equals(userOfAccount.getId())) {
                ApiExceptionCast.cast(UserCode.ACCOUNT_ALREADY_EXISTS);
            }
        }
    }

    @Override
    @Transactional
    public BasicResponseData updateAccount(Long accountId, UserAccount userAccount) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        userAccountRepository.findById(accountId).ifPresent(account -> {
            if (account.getUserId().equals(userId)) { // admin..
                if (!StringUtils.hasText(userAccount.getAccount())) {
                    ApiExceptionCast.invalidParam();
                }
                verifyOldCredential(account, userAccount);
                checkUniqueAccount(userId, userAccount);
                account.setAccount(userAccount.getAccount());
                String credential = checkAndGetCredential(userAccount);
                account.setCredential(credential);
                account.setType(userAccount.getType());
            }
            userAccountRepository.save(account);
        });
        return BasicResponseData.ok();
    }

    /**
     * 账户类型为密码，检查密码是否相等
     * @param account 数据库查出的账户
     * @param userAccount 用户请求的账户
     */
    private void verifyOldCredential(UserAccount account, UserAccount userAccount) {
        if (UserAccountType.PASSWORD.getType().equals(userAccount.getType())) {
            if (!StringUtils.hasText(userAccount.getOldCredential()) ||
                !passwordEncoder.matches(userAccount.getOldCredential(), account.getCredential())) {
                ApiExceptionCast.cast(UserCode.CREDENTIAL_NOT_CORRECT);
            }
        }
    }

    @Override
    public BasicResponseData closeAccount(Long accountId) {
        Long total = findCurrentAccounts().getPayload().getTotal();
        if (total <= 1) {
            ApiExceptionCast.cast(UserCode.AT_LEAST_ONE_ACCOUNT);
        }
        userAccountRepository.findById(accountId).ifPresent(userAccount -> {
            userAccount.setStatus(false);
            userAccountRepository.save(userAccount);
        });
        return BasicResponseData.ok();
    }

    @Override
    public UserOfAccount findUserByAccountAndType(String account, UserAccountType type) {
        return userAccountMapper.selectUserByAccountAndType(account, type.getType());
    }

    /**
     * 创建新用户与账户
     * 由于查询方法使用MyBatis，不在同一个Connection，需要提交失误后由远程端发起查询
     * @param userOfAccount
     * @return
     */
    @Override
    @Transactional
    public BasicResponseData createUserWithDefaultRole(UserOfAccount userOfAccount) {
        if (Objects.isNull(userOfAccount)) {
            return BasicResponseData.invalid();
        }

        // 保存用户
        User user = new User();
        BeanUtils.copyProperties(userOfAccount, user);
        user.setStatus(true);
        user = userRepository.save(user);

        // 保存账号
        UserAccount userAccount = userOfAccount.getUserAccount();
        userAccount.setStatus(true);
        userAccount.setUserId(user.getId());
        userAccountRepository.save(userAccount);

        // 赋予权限
        Role role = roleRepository.findByCode(RoleConstants.DEFAULT_ROLE_CODE);
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRoleRepository.save(userRole);

        return BasicResponseData.ok();
    }
}
