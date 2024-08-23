package com.fluffytime.user.exception;

import com.fluffytime.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LoginErrorCode implements ErrorCode {
    MISMATCHED_PASSWORD(HttpStatus.UNAUTHORIZED, "LE-001", "비밀번호가 일치하지 않습니다."),
    LOGIN_TYPE_ERROR(HttpStatus.BAD_REQUEST,"LE-002","일반, 소셜 로그인 타입을 확인해주세요.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
