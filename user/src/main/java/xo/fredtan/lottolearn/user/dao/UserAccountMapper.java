package xo.fredtan.lottolearn.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xo.fredtan.lottolearn.domain.user.response.UserOfAccount;

@Mapper
public interface UserAccountMapper {
    UserOfAccount selectUserByAccountAndType(@Param("account") String account, @Param("type") Integer type);
}
