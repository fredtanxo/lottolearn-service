package xo.fredtan.lottolearn.domain.user.response;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.response.ResponseData;
import xo.fredtan.lottolearn.domain.user.Menu;
import xo.fredtan.lottolearn.domain.user.Role;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.UserAccount;

import java.util.List;

@Data
public class UserOfAccount extends User implements ResponseData {
    private UserAccount userAccount;
    private List<Role> roleList;
    private List<Menu> menuList;
}
