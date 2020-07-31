package xo.fredtan.lottolearn.user.dao;

import org.apache.ibatis.annotations.Mapper;
import xo.fredtan.lottolearn.domain.user.response.UserWithRoleIds;

@Mapper
public interface UserRoleMapper {
    UserWithRoleIds selectUserWithRole(String userId);
}
