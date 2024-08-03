package com.fluffytime.myPage.exception;


import com.fluffytime.myPage.dto.ErrorResponseDto;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyPageExceptionResponseHandler {

    // 마이페이지에서 발생하는 모든 예외 처리를 중앙 집중화 하여 처리
    @ExceptionHandler(MyPageException.class)
    public ErrorResponseDto mypageException(MyPageException exception) {
        // ErrorResponseDto의 of 메서드를 통해 매개변수로 받은 code와 message를 이용하여 응답 dto 객체 생성
        return ErrorResponseDto.of(exception.getCode(), exception.getMessage());
    }

}
