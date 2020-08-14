package xo.fredtan.lottolearn.domain.processor.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;

@Data
public class MediaProcessRequest implements RequestData {
    private String resourceId;
}
