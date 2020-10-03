package xo.fredtan.lottolearn.user.dao;

import org.apache.ibatis.annotations.Mapper;
import xo.fredtan.lottolearn.domain.user.Role;

@Mapper
public interface PermissionMapper {
    Role selectRoleWithMenu(Long roleId);
}
