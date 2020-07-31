package xo.fredtan.lottolearn.api.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.Role;
import xo.fredtan.lottolearn.domain.user.request.ModifyRoleRequest;
import xo.fredtan.lottolearn.domain.user.response.RoleWithMenuIds;

@Api("角色管理")
public interface RoleControllerApi {
    @ApiOperation("查询所有角色")
    QueryResponseData<Role> findAllRoles(Integer page, Integer size);

    @ApiOperation("根据角色ID查询角色信息（包含角色权限信息）")
    UniqueQueryResponseData<RoleWithMenuIds> findRoleById(String roleId);

    @ApiOperation("增加角色")
    BasicResponseData addRole(ModifyRoleRequest modifyRoleRequest);

    @ApiOperation("更新角色")
    BasicResponseData updateRole(String roleId, ModifyRoleRequest modifyRoleRequest);

    @ApiOperation("关闭角色")
    BasicResponseData closeRole(String roleId);
}
