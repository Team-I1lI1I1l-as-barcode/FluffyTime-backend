package com.fluffytime.mypage.exception;


import com.fluffytime.mypage.response.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MyPageExceptionResponseHandler {

    // 마이페이지에서 발생하는 모든 예외 처리를 중앙 집중화 하여 처리
    @ExceptionHandler(MyPageException.class)
    public ErrorDto mypageException(MyPageException exception) {
        log.info("마이페이지 사용자 예외 발생 >>> 에러 코드 : " + exception.getCode() + "에러 메시지 : "
            + exception.getMessage());

        // ErrorResponseDto의 of 메서드를 통해 매개변수로 받은 code와 message를 이용하여 응답 dto 객체 생성
        return ErrorDto.of(exception.getCode(), exception.getMessage());
    }

}
