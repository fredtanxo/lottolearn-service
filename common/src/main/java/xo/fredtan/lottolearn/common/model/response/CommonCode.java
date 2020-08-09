package xo.fredtan.lottolearn.common.model.response;

public enum CommonCode implements ResultCode {
    OK(200, "OK"),
    INVALID_PARAM(400, "非法参数"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权执行此操作"),
    INTERNAL_ERROR(500, "内部错误");

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
