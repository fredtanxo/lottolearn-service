package xo.fredtan.lottolearn.user.dao;

import org.apache.ibatis.annotations.Mapper;
import xo.fredtan.lottolearn.domain.user.User;

@Mapper
public interface UserRoleMapper {
    User selectUserWithRole(Long userId);
}
