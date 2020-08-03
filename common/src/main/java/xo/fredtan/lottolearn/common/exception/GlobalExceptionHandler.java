package xo.fredtan.lottolearn.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiInvocationException.class)
    public BasicResponseData handleApiInvocationException(ApiInvocationException e) {
        log.error("{}\n{}", e.getMessage(), e.getStackTrace());
        return new BasicResponseData(e.getResultCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BasicResponseData handleFieldNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        return Objects.nonNull(fieldError) ?
                new BasicResponseData(400, fieldError.getDefaultMessage()) : BasicResponseData.invalid();
    }

    @ExceptionHandler(Exception.class)
    public BasicResponseData handleGlobalException(Exception e) {
        log.error(e.getMessage());
        return BasicResponseData.error();
    }
}
