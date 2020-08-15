package xo.fredtan.lottolearn.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.user.constant.RoleConstants;
import xo.fredtan.lottolearn.api.user.constant.UserAccountType;
import xo.fredtan.lottolearn.api.user.service.UserAccountService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.domain.user.Role;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.UserAccount;
import xo.fredtan.lottolearn.domain.user.UserRole;
import xo.fredtan.lottolearn.domain.user.response.UserOfAccount;
import xo.fredtan.lottolearn.user.dao.*;

import java.util.Objects;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountMapper userAccountMapper;
    private final UserAccountRepository userAccountRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public QueryResponseData<UserAccount> findAllUserAccount(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserAccount> accounts = userAccountRepository.findAll(pageRequest);

        QueryResult<UserAccount> queryResult = new QueryResult<>(accounts.getTotalElements(), accounts.getContent());
        return QueryResponseData.ok(queryResult);
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
