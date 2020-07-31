package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.Role;
import xo.fredtan.lottolearn.domain.user.request.ModifyRoleRequest;
import xo.fredtan.lottolearn.domain.user.response.RoleWithMenuIds;

public interface RoleService {
    QueryResponseData<Role> findAllRoles(Integer page, Integer size);

    UniqueQueryResponseData<RoleWithMenuIds> findRoleById(String roleId);

    BasicResponseData addRole(ModifyRoleRequest modifyRoleRequest);

    BasicResponseData updateRole(String roleId, ModifyRoleRequest modifyRoleRequest);

    BasicResponseData closeRole(String roleId);
}
