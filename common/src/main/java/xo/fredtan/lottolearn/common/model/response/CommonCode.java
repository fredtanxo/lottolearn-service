package xo.fredtan.lottolearn.common.model.response;

public enum CommonCode implements ResultCode {
    OK(200, "Ok"),
    INVALID_PARAM(400, "Invalid param"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Permission denied"),
    INTERNAL_ERROR(500, "Internal error");

    public final Integer code;
    public final String message;

    CommonCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
