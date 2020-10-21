package xo.fredtan.lottolearn.domain.course.request;

import lombok.Data;

@Data
public class PostDiscussionRequest {
    private String content;
    private Long replyTo;
}
