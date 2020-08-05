package xo.fredtan.lottolearn.domain.course.response;

import xo.fredtan.lottolearn.common.model.response.ResultCode;

public enum CourseCode implements ResultCode {
    ADD_SUCCESS(200, "添加成功"),
    JOIN_SUCCESS(200, "加入成功"),
    COURSE_NOT_EXISTS(400, "课程不存在"),
    COURSE_IS_CLOSED(400, "课程已结束");

    public final Integer code;
    public final String message;

    CourseCode(Integer code, String message) {
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
