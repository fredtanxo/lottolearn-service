package xo.fredtan.lottolearn.domain.course.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;

@Data
public class ModifyAnnouncementRequest implements RequestData {
    private String title;
    private String content;
    private String publisher;
}
