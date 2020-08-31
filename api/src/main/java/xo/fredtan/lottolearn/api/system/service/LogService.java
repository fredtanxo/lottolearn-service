package xo.fredtan.lottolearn.api.system.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.system.Log;
import xo.fredtan.lottolearn.domain.system.request.QueryLogRequest;

public interface LogService {
    QueryResponseData<Log> findAllLogs(Integer page, Integer size, QueryLogRequest queryLogRequest);

    void saveLog(Log log);

    BasicResponseData deleteLog(String logId);
}
