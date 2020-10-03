package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.Role;

public interface RoleService {
    QueryResponseData<Role> findAllRoles(Integer page, Integer size);

    UniqueQueryResponseData<Role> findRoleById(Long roleId);

    BasicResponseData addRole(Role role);

    BasicResponseData updateRole(Long roleId, Role role);

    BasicResponseData closeRole(Long roleId);
}
