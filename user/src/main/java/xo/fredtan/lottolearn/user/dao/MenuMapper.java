package xo.fredtan.lottolearn.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xo.fredtan.lottolearn.domain.user.response.MenuTree;

import java.util.List;

@Mapper
public interface MenuMapper {
    @Select("select * from menu")
    List<MenuTree> selectMenus();

    @Select("select m.id from user_role ur left join permission p on (ur.role_id = p.role_id) " +
            "left join menu m on (p.menu_id = m.id) " +
            "where m.`status`=1 and ur.user_id = #{userId}")
    List<String> selectUserPermissions(@Param("userId") String userId);
}
