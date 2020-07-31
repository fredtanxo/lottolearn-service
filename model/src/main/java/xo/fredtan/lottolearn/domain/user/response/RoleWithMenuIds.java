package xo.fredtan.lottolearn.domain.user.response;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;
import xo.fredtan.lottolearn.domain.user.Role;

import java.util.List;

@Data
public class RoleWithMenuIds extends Role implements RequestData {
    private List<String> menuIds;
}
