package xo.fredtan.lottolearn.domain.user.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;
import xo.fredtan.lottolearn.domain.user.Role;

import java.util.List;

@Data
public class ModifyRoleRequest extends Role implements RequestData {
    private List<Long> menuIds;
}
