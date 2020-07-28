package xo.fredtan.lottolearn.common.model.response;

import lombok.Data;

@Data
public class BasicResponseData implements ResponseData {
    public Integer code;
    public String message;

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
}
