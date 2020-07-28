package xo.fredtan.lottolearn.common.exception;

import xo.fredtan.lottolearn.common.model.response.CommonCode;
import xo.fredtan.lottolearn.common.model.response.ResultCode;

public class ApiExceptionCast {
    public static void cast(ResultCode resultCode) {
        throw new ApiInvocationException(resultCode);
    }

    public static void invalidParam() {
        throw new ApiInvocationException(CommonCode.INVALID_PARAM);
    }

    public static void unauthorized() {
        throw new ApiInvocationException(CommonCode.UNAUTHORIZED);
    }

    public static void forbidden() {
        throw new ApiInvocationException(CommonCode.FORBIDDEN);
    }

    public static void internalError() {
        throw new ApiInvocationException(CommonCode.INTERNAL_ERROR);
    }
}
