package xo.fredtan.lottolearn.domain.course.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;

@Data
public class JoinCourseRequest implements RequestData {
    private String id;
    private Long userId;
    private String invitationCode;
}
