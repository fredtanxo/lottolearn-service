package xo.fredtan.lottolearn.domain.auth.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;

@Data
public class FormLoginRequest implements RequestData {
    private String username;
    private String password;
}
