package xo.fredtan.lottolearn.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xo.fredtan.lottolearn.domain.user.UserAccount;
import xo.fredtan.lottolearn.domain.user.response.UserOfAccount;

import java.util.List;

@Mapper
public interface UserAccountMapper {
    @Select("select id, account, type from user_account where id = #{accountId} and status = 1")
    UserAccount selectUserAccountById(@Param("accountId") Long accountId);

    @Select("select id, account, type from user_account where user_id = #{userId} and status = 1")
    List<UserAccount> selectAllAccountsByUserId(@Param("userId") Long userId);

    UserOfAccount selectUserByAccountAndType(@Param("account") String account, @Param("type") Integer type);
}
