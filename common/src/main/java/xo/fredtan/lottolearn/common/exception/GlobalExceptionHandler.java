package xo.fredtan.lottolearn.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiInvocationException.class)
    public BasicResponseData handleGlobalException(ApiInvocationException e) {
        log.error("{}\n{}", e.getMessage(), e.getStackTrace());
        return new BasicResponseData(e.getResultCode());
    }
}
