package xo.fredtan.lottolearn.domain.course.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;

@Data
public class QueryCourseRequest implements RequestData {
    private String name;
    private Long termId;
    private Boolean mode;
    private Integer status;
}
