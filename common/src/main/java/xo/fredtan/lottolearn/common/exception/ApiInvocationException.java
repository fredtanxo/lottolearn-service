package xo.fredtan.lottolearn.common.exception;

import lombok.Getter;
import xo.fredtan.lottolearn.common.model.response.ResultCode;

public class ApiInvocationException extends RuntimeException {
    @Getter
    private final ResultCode resultCode;

    public ApiInvocationException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
