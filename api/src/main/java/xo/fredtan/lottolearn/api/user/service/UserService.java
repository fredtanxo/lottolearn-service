package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.request.ModifyUserRequest;
import xo.fredtan.lottolearn.domain.user.request.QueryUserRequest;
import xo.fredtan.lottolearn.domain.user.response.UserWithRoleIds;

public interface UserService {
    QueryResponseData<User> findAllUsers(Integer page, Integer size, QueryUserRequest queryUserRequest);

    UniqueQueryResponseData<UserWithRoleIds> findUserById(Long userId);

    UniqueQueryResponseData<UserWithRoleIds> findUserByIdWithRoleIds(Long userId);

    UniqueQueryResponseData<User> findCurrentUser();

    BasicResponseData addUser(ModifyUserRequest modifyUserRequest);

    BasicResponseData updateUser(Long userId, ModifyUserRequest modifyUserRequest);

    BasicResponseData closeUser(Long userId);
}
