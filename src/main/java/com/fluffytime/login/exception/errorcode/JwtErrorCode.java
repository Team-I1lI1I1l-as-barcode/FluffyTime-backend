package com.fluffytime.login.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    UNKNOWN_ERROR(HttpStatus.UNAUTHORIZED, "401", "unknown error"),
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "401", "not found token"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "401", "invalid token"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "401", "expired token"),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "401", "unsupported token"),
    JWT_FILTER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "JWT filter internal server error");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
