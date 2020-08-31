package xo.fredtan.lottolearn.api.system.controller;

import io.swagger.annotations.Api;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.system.Log;
import xo.fredtan.lottolearn.domain.system.request.QueryLogRequest;

@Api("系统日志")
public interface LogControllerApi {
    QueryResponseData<Log> findAllLogs(Integer page, Integer size, QueryLogRequest queryLogRequest);
}
