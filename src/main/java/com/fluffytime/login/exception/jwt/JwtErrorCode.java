package com.fluffytime.login.exception.jwt;

import com.fluffytime.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    // 알 수 없는 에러
    UNKNOWN_ERROR(HttpStatus.UNAUTHORIZED, "JWTE-001", "unknown error"),
    // headers에 토큰 형식의 값을 찾을 수 없음
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "JWTE-002", "not found token"),
    // 유효하지 않은 토큰
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "JWTE-003", "invalid token"),
    // 기간이 만료된 토큰
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "JWTE-004", "expired token"),
    // 지원하지 않는 토큰
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "JWTE-005", "unsupported token"),
    JWT_FILTER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JWTE-006",
        "JWT filter internal server error");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
