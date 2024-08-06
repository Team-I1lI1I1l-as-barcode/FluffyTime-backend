package com.fluffytime.join.exception;

import com.fluffytime.join.exception.errorcode.ErrorCode;
import com.fluffytime.join.exception.errorcode.GlobalErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidRequestHandle(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        return createErrorResponseEntity(GlobalErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> invalidRequestHandle(HandlerMethodValidationException e) {
        log.error("HandlerMethodValidationException", e);
        return createErrorResponseEntity(GlobalErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(FluffyException.class)
    protected ResponseEntity<ErrorResponse> handle(FluffyException e) {
        log.error("FluffyException", e);
        return createErrorResponseEntity(e.getErrorCode());
    }

//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity<ErrorResponse> handle(Exception e) {
//        e.printStackTrace();
//        log.error("Exception", e);
//        return createErrorResponseEntity(GlobalErrorCode.INTERNAL_SERVER_ERROR);
//    }

    private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.of(errorCode));
    }
}
