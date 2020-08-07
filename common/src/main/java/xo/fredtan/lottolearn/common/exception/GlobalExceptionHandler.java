package xo.fredtan.lottolearn.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BasicResponseData> handleApiInvocationException(ApiInvocationException e) {
        log.error("API执行异常：{}", e.getResultCode());
        log.error(e.getMessage(), e);
        BasicResponseData responseData = new BasicResponseData(e.getResultCode());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HttpEntity<BasicResponseData> handleFieldNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        BasicResponseData responseData = Objects.nonNull(fieldError) ?
                new BasicResponseData(400, fieldError.getDefaultMessage()) : BasicResponseData.invalid();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
    }

    @ExceptionHandler(Exception.class)
    public HttpEntity<BasicResponseData> handleGlobalException(Exception e) {
        log.error("未定义异常", e);
        BasicResponseData responseData = BasicResponseData.error();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
    }
}
