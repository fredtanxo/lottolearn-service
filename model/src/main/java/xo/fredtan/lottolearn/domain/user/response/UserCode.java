package xo.fredtan.lottolearn.domain.user.response;

import xo.fredtan.lottolearn.common.model.response.ResultCode;

public enum UserCode implements ResultCode {
    /* 账户相关 */
    ACCOUNT_ALREADY_EXISTS(400, "用户名已存在"),
    CREDENTIAL_NOT_CORRECT(400, "密码错误"),
    AT_LEAST_ONE_ACCOUNT(400, "至少需要保留一种登录方式");

    public final Integer code;
    public final String message;

    UserCode(Integer code, String message) {
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
