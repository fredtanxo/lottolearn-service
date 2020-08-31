package xo.fredtan.lottolearn.system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xo.fredtan.lottolearn.api.system.controller.LogControllerApi;
import xo.fredtan.lottolearn.api.system.service.LogService;
import xo.fredtan.lottolearn.common.annotation.ValidatePagination;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.system.Log;
import xo.fredtan.lottolearn.domain.system.request.QueryLogRequest;

@RestController
@RequestMapping("/log")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LogController implements LogControllerApi {
    private final LogService logService;

    @Override
    @GetMapping("/all")
    @ValidatePagination
    public QueryResponseData<Log> findAllLogs(Integer page, Integer size, QueryLogRequest queryLogRequest) {
        return logService.findAllLogs(page, size, queryLogRequest);
    }
}
