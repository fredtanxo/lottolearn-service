package xo.fredtan.lottolearn.domain.course.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;

@Data
public class QueryDiscussionRequest implements RequestData {
    private Boolean reverse;
    private Boolean trend;
}
