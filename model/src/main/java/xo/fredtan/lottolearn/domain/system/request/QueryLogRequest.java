package xo.fredtan.lottolearn.domain.system.request;

import lombok.Data;
import xo.fredtan.lottolearn.common.model.request.RequestData;

import java.util.Date;

@Data
public class QueryLogRequest implements RequestData {
    private Date from;
    private Date to;
}
