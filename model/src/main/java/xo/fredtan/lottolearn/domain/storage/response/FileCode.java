package xo.fredtan.lottolearn.domain.storage.response;

import xo.fredtan.lottolearn.common.model.response.ResultCode;

public enum FileCode implements ResultCode {
    FILE_ALREADY_EXISTS(400, "文件已存在"),
    FILE_NOT_FOUND(404, "文件不存在"),
    FILE_CREATION_FAIL(500, "创建文件失败");

    public final Integer code;
    public final String message;

    FileCode(Integer code, String message) {
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
