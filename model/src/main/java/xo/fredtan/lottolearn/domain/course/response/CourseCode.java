package xo.fredtan.lottolearn.domain.course.response;

import xo.fredtan.lottolearn.common.model.response.ResultCode;

public enum CourseCode implements ResultCode {
    /* 课程相关 */
    ADD_SUCCESS(200, "添加成功"),
    JOIN_SUCCESS(200, "加入成功"),
    COURSE_NOT_EXISTS(400, "课程不存在"),
    COURSE_IS_CLOSED(400, "课程已结束"),
    ALREADY_JOINED(400, "已经加入过该课程"),
    ADD_FAILED(500, "添加失败"),
    JOIN_FAILED(500, "加入失败"),
    /* 章节相关 */
    NOT_JOIN_COURSE(400, "未加入此课程");

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
