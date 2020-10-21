package xo.fredtan.lottolearn.domain.course.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;
import xo.fredtan.lottolearn.domain.course.Course;

@Data
public class AddCourseRequest implements RequestData {
    private String id;
    private Long userId;
    private Course course;
}
