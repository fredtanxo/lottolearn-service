package xo.fredtan.lottolearn.domain.course.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;
import xo.fredtan.lottolearn.domain.course.SignRecord;

@Data
public class CourseSignRequest extends SignRecord implements RequestData {
}
