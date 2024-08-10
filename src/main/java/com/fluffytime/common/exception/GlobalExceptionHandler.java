package com.fluffytime.common.exception;


import com.fluffytime.common.exception.business.FluffyException;
import com.fluffytime.common.exception.global.GlobalErrorCode;
import com.fluffytime.common.exception.global.GlobalException;
import com.fluffytime.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice//모든 컨트롤러에서 발생하는 예외를 잡아줌
public class GlobalExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> invalidRequestHandle(HandlerMethodValidationException e) {
        log.error("HandlerMethodValidationException = {}", e.getMessage());
        return createErrorResponseEntity(GlobalErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidRequestHandle(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException = {}", e.getMessage());
        return createErrorResponseEntity(GlobalErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(FluffyException.class)
    protected ResponseEntity<ErrorResponse> handle(FluffyException e) {
        log.error("FluffyException = {}", e.getMessage());
        return createErrorResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(GlobalException.class)
    protected ResponseEntity<ErrorResponse> handle(GlobalException e) {
        log.error("GlobalException = {}", e.getMessage());
        return createErrorResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        log.error("BadCredentialsException = {}", e.getMessage());
        return createErrorResponseEntity(GlobalErrorCode.NOT_ACCESS_PERMISSION);
    }

    private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode code) {
        return ResponseEntity.status(code.getHttpStatus()).body(ErrorResponse.of(code));
    }
}
