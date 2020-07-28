package xo.fredtan.lottolearn.api.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.domain.user.Role;
import xo.fredtan.lottolearn.domain.user.request.ModifyRoleRequest;

@Api("角色管理")
public interface RoleControllerApi {
    @ApiOperation("查询所有角色")
    QueryResponseData<Role> findAllRoles(Integer page, Integer size);

    @ApiOperation("增加角色")
    BasicResponseData addRole(ModifyRoleRequest modifyRoleRequest);

    @ApiOperation("更新角色")
    BasicResponseData updateRole(String roleId, ModifyRoleRequest modifyRoleRequest);

    @ApiOperation("关闭角色")
    BasicResponseData closeRole(String roleId);
}
