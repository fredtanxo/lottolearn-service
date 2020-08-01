package xo.fredtan.lottolearn.domain.course.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;

@Data
public class QueryUserCourseRequest implements RequestData {
    private Integer status;
    private Boolean teacher;
}
