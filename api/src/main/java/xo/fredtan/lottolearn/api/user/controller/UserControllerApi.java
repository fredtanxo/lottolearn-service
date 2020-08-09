package xo.fredtan.lottolearn.api.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.request.ModifyUserRequest;
import xo.fredtan.lottolearn.domain.user.request.QueryUserRequest;
import xo.fredtan.lottolearn.domain.user.response.UserWithRoleIds;

@Api("用户管理")
public interface UserControllerApi {
    @ApiOperation("查询所有用户")
    QueryResponseData<User> findAllUsers(Integer page, Integer size, QueryUserRequest queryUserRequest);

    @ApiOperation("根据用户ID查询用户信息")
    UniqueQueryResponseData<UserWithRoleIds> findUserById(String userId, Boolean withRoles);

    @ApiOperation("增加用户")
    BasicResponseData addUser(ModifyUserRequest modifyUserRequest);

    @ApiOperation("更新用户")
    BasicResponseData updateUser(String userId, ModifyUserRequest modifyUserRequest);

    @ApiOperation("关闭用户")
    BasicResponseData closeUser(String userId);
}
