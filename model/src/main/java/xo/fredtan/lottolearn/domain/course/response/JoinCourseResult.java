package xo.fredtan.lottolearn.domain.course.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.ResultCode;

@Data
@AllArgsConstructor
public class JoinCourseResult extends BasicResponseData {
    private String courseId;

    public JoinCourseResult(ResultCode resultCode, String courseId) {
        super(resultCode);
        this.courseId = courseId;
    }
}
