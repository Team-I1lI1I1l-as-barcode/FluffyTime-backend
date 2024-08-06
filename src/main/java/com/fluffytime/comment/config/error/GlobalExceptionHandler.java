package com.fluffytime.comment.config.error;

import com.fluffytime.comment.config.error.exception.PostNotFoundException;
import com.fluffytime.comment.config.error.exception.UserNotFoundException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice //모든 컨트롤러에서 발생하는 예외를 잡아줌
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handle(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException", e);
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

//    @ExceptionHandler(RuntimeException.class)
//    protected ResponseEntity<ErrorResponse> handle(RuntimeException e) {
//        log.error("RuntimeException", e);
//        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
//        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
//        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
//    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handle(UserNotFoundException e) {
        log.error("UserNotFoundException", e);
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    @ExceptionHandler(PostNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handle(PostNotFoundException e) {
        log.error("PostNotFoundException", e);
        ErrorCode errorCode = ErrorCode.POST_NOT_FOUND;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE,
            errors.toString());
        return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }
}
