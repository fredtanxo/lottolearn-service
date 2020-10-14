package xo.fredtan.lottolearn.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xo.fredtan.lottolearn.domain.user.Role;
import xo.fredtan.lottolearn.domain.user.User;

import java.util.List;

@Mapper
public interface UserRoleMapper {
    User selectUserWithRoleIds(@Param("userId") Long userId);

    @Select("select r.id, r.name, r.code from user_role ur left join role r on ur.role_id = r.id where ur.user_id = #{userId}")
    List<Role> selectUserRoles(@Param("userId") Long userId);
}
