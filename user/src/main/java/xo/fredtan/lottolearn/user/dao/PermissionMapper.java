package xo.fredtan.lottolearn.user.dao;

import org.apache.ibatis.annotations.Mapper;
import xo.fredtan.lottolearn.domain.user.response.RoleWithMenuIds;

@Mapper
public interface PermissionMapper {
    RoleWithMenuIds selectRoleWithMenu(Long roleId);
}
