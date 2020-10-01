package xo.fredtan.lottolearn.domain.course.response;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.ResultCode;

@Data
public class AddCourseResult extends BasicResponseData {
    private String invitationCode;
    private Long courseId;

    public AddCourseResult(ResultCode resultCode, String invitationCode, Long courseId) {
        super(resultCode);
        this.invitationCode = invitationCode;
        this.courseId = courseId;
    }
}
