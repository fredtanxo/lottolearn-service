package xo.fredtan.lottolearn.domain.course.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;

@Data
public class PostDiscussionRequest implements RequestData {
    private String content;
    private Long replyTo;
}
