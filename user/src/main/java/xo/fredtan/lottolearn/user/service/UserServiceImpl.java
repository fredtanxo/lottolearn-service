package xo.fredtan.lottolearn.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.user.service.UserService;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.UserRole;
import xo.fredtan.lottolearn.domain.user.request.ModifyUserRequest;
import xo.fredtan.lottolearn.domain.user.request.QueryUserRequest;
import xo.fredtan.lottolearn.user.dao.UserRepository;
import xo.fredtan.lottolearn.user.dao.UserRoleRepository;

import java.util.*;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public QueryResponseData<User> findAllUsers(Integer page, Integer size, QueryUserRequest queryUserRequest) {
        PageRequest pageRequest = PageRequest.of(page, size);

        if (Objects.isNull(queryUserRequest))
            queryUserRequest = new QueryUserRequest();

        User user = new User();
        user.setNickname(queryUserRequest.getNickname());
        user.setStatus(queryUserRequest.getStatus());

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nickname", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<User> example = Example.of(user, exampleMatcher);
        Page<User> users = userRepository.findAll(example, pageRequest);

        QueryResult<User> queryResult = new QueryResult<>(users.getTotalElements(), users.getContent());
        return QueryResponseData.ok(queryResult);
    }

    @Override
    public UniqueQueryResponseData<User> findUserById(String userId) {
        return userRepository.findById(userId).map(UniqueQueryResponseData<User>::new).orElse(null);
    }

    @Override
    @Transactional
    public BasicResponseData addUser(ModifyUserRequest modifyUserRequest) {
        User user = new User();
        BeanUtils.copyProperties(modifyUserRequest, user);
        user.setId(null);
        User save = userRepository.save(user);

        modifyUserRequest.getRoleIds().forEach(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(save.getId());
            userRole.setRoleId(roleId);
            userRoleRepository.save(userRole);
        });
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateUser(String userId, ModifyUserRequest modifyUserRequest) {
        userRepository.findById(userId).ifPresent(user -> {
            BeanUtils.copyProperties(modifyUserRequest, user);
            user.setId(userId);
            userRepository.save(user);
            updateRoles(userId, modifyUserRequest.getRoleIds());
        });
        return BasicResponseData.ok();
    }

    private void updateRoles(String userId, List<String> roleIds) {
        if (Objects.isNull(roleIds))
            return;
        userRoleRepository.deleteByUserId(userId);
        roleIds.forEach(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleRepository.save(userRole);
        });
    }

    @Override
    @Transactional
    public BasicResponseData closeUser(String userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(false);
            userRepository.save(user);
        });
        return BasicResponseData.ok();
    }
}
