package com.fluffytime.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice//모든 컨트롤러에서 발생하는 예외를 잡아줌
public class GlobalExceptionHandler {

}
