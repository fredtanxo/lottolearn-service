package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.request.QueryUserRequest;

public interface UserService {
    QueryResponseData<User> findAllUsers(Integer page, Integer size, QueryUserRequest queryUserRequest);

    UniqueQueryResponseData<User> findUserById(Long userId);

    UniqueQueryResponseData<User> findUserByIdWithRoleIds(Long userId);

    UniqueQueryResponseData<User> findCurrentUser();

    BasicResponseData addUser(User user);

    BasicResponseData updateUser(Long userId, User user);

    BasicResponseData closeUser(Long userId);
}
