package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.request.ModifyUserRequest;
import xo.fredtan.lottolearn.domain.user.request.QueryUserRequest;

public interface UserService {
    QueryResponseData<User> findAllUsers(Integer page, Integer size, QueryUserRequest queryUserRequest);

    UniqueQueryResponseData<User> findUserById(String userId);

    BasicResponseData addUser(ModifyUserRequest modifyUserRequest);

    BasicResponseData updateUser(String userId, ModifyUserRequest modifyUserRequest);

    BasicResponseData closeUser(String userId);
}
