package xo.fredtan.lottolearn.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.user.constants.UserConstants;
import xo.fredtan.lottolearn.api.user.service.UserService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.common.util.ProtostuffSerializeUtils;
import xo.fredtan.lottolearn.common.util.RedisCacheUtils;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.UserRole;
import xo.fredtan.lottolearn.domain.user.request.QueryUserRequest;
import xo.fredtan.lottolearn.user.dao.UserRepository;
import xo.fredtan.lottolearn.user.dao.UserRoleMapper;
import xo.fredtan.lottolearn.user.dao.UserRoleRepository;

import java.util.*;

@Service
@DubboService
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRoleMapper userRoleMapper;

    private final RedisTemplate<String, byte[]> byteRedisTemplate;

    private static final Random random = new Random();

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
    public UniqueQueryResponseData<User> findUserById(Long userId) {
        User user = findCachedUserById(userId);
        return UniqueQueryResponseData.ok(user);
    }

    /**
     * 根据用户ID批量获取用户信息
     * @param userIds 用户ID列表
     * @return Key为{@code userId}，value为{@link User}的Map
     */
    @Override
    public Map<Long, User> batchFindUserById(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        Map<Long, User> map = new HashMap<>(userIds.size());
        users.forEach(user -> map.put(user.getId(), user));
        return map;
    }

    @Override
    public UniqueQueryResponseData<User> findUserByIdWithRoleIds(Long userId) {
        User userWithRolesIds = userRoleMapper.selectUserWithRoleIds(userId);
        return UniqueQueryResponseData.ok(userWithRolesIds);
    }

    @Override
    public UniqueQueryResponseData<User> findCurrentUser() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        User user = findCachedUserById(userId);
        return UniqueQueryResponseData.ok(user);
    }

    private User findCachedUserById(Long userId) {
        BoundValueOperations<String, byte[]> ops = byteRedisTemplate.boundValueOps(UserConstants.USER_CACHE_PREFIX + userId);
        byte[] bytes = ops.get();
        User user;
        if (Objects.nonNull(bytes)) {
            user = ProtostuffSerializeUtils.deserialize(bytes, User.class);
        } else {
            user = userRepository.findById(userId).orElse(null);
            if (Objects.nonNull(user)) {
                ops.set(
                        ProtostuffSerializeUtils.serialize(user),
                        UserConstants.USER_CACHE_EXPIRATION.plusDays(random.nextInt(3))
                );
            }
        }
        return user;
    }

    @Override
    @Transactional
    public BasicResponseData addUser(User user) {
        user.setId(null);
        User save = userRepository.save(user);
        user.getRoleIds().forEach(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(save.getId());
            userRole.setRoleId(roleId);
            userRoleRepository.save(userRole);
        });
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateUser(Long userId, User user) {
        userRepository.findById(userId).ifPresent(u -> {
            if (StringUtils.hasText(user.getNickname())) {
                u.setNickname(user.getNickname());
            }
            u.setAvatar(user.getAvatar());
            u.setGender(user.getGender());
            u.setDescription(user.getDescription());
            userRepository.save(u);
            updateRoles(userId, user.getRoleIds());
        });

        // 清除缓存
        RedisCacheUtils.clearCache(UserConstants.USER_CACHE_PREFIX + userId, byteRedisTemplate);

        return BasicResponseData.ok();
    }

    private void updateRoles(Long userId, List<Long> roleIds) {
        if (Objects.isNull(roleIds)) {
            return;
        }
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
    public BasicResponseData closeUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(false);
            userRepository.save(user);
        });
        return BasicResponseData.ok();
    }
}
