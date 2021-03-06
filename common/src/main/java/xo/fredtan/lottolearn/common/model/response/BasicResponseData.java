package xo.fredtan.lottolearn.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicResponseData implements ResponseData {
    private Integer code;
    private String message;

    public BasicResponseData(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public static BasicResponseData ok() {
        return new BasicResponseData(CommonCode.OK);
    }

    public static BasicResponseData error() {
        return new BasicResponseData(CommonCode.INTERNAL_ERROR);
    }

    public static BasicResponseData invalid() {
        return new BasicResponseData(CommonCode.INVALID_PARAM);
    }
}
