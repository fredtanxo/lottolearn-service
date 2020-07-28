package xo.fredtan.lottolearn.domain.user.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;

@Data
public class QueryUserRequest implements RequestData {
    private String nickname;
    private Boolean status;
}
