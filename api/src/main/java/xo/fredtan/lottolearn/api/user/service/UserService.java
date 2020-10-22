package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.request.QueryUserRequest;

import java.util.List;
import java.util.Map;

public interface UserService {
    QueryResponseData<User> findAllUsers(Integer page, Integer size, QueryUserRequest queryUserRequest);

    UniqueQueryResponseData<User> findUserById(Long userId);

    Map<Long, User> batchFindUserById(List<Long> userIds);

    UniqueQueryResponseData<User> findUserByIdWithRoleIds(Long userId);

    UniqueQueryResponseData<User> findCurrentUser();

    BasicResponseData addUser(User user);

    BasicResponseData updateUser(Long userId, User user);

    BasicResponseData closeUser(Long userId);
}
