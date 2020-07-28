package xo.fredtan.lottolearn.domain.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import xo.fredtan.lottolearn.common.model.response.ResponseData;

import java.util.Set;

@Data
@AllArgsConstructor
public class PermissionCodeSet implements ResponseData {
    Set<String> permissionCodes;
}
