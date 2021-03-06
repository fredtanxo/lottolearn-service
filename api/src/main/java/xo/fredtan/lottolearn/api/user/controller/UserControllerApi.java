package xo.fredtan.lottolearn.api.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.request.QueryUserRequest;

@Api("用户管理")
public interface UserControllerApi {
    @ApiOperation("查询所有用户")
    QueryResponseData<User> findAllUsers(Integer page, Integer size, QueryUserRequest queryUserRequest);

    @ApiOperation("根据用户ID查询用户信息")
    UniqueQueryResponseData<User> findUserById(Long userId, Boolean withRoles);

    @ApiOperation("查询当前用户")
    UniqueQueryResponseData<User> findCurrentUser();

    @ApiOperation("增加用户")
    BasicResponseData addUser(User user);

    @ApiOperation("更新用户")
    BasicResponseData updateUser(Long userId, User user);

    @ApiOperation("关闭用户")
    BasicResponseData closeUser(Long userId);
}
