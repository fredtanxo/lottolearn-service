package xo.fredtan.lottolearn.domain.user.response;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.response.ResponseData;
import xo.fredtan.lottolearn.domain.user.User;

import java.util.List;

@Data
public class UserWithRoleIds extends User implements ResponseData {
    private List<String> roleIds;
}
