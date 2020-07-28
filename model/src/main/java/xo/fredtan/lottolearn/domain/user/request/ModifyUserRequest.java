package xo.fredtan.lottolearn.domain.user.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;
import xo.fredtan.lottolearn.domain.user.User;

import java.util.List;

@Data
public class ModifyUserRequest extends User implements RequestData {
    private List<String> roleIds;
}
