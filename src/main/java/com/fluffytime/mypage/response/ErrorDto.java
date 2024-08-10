package com.fluffytime.mypage.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
// 에러가 발생했을시 클라이언트에게 보내는 에러 DTO
public class ErrorDto {

    private final String code; // 상태코드
    private final String message; // 에러메시지

    // ErrorDto 생성
    public static ErrorDto of(String code, String message) {
        return new ErrorDto(code, message);
    }

}
