package com.fluffytime.login.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LoginErrorCode implements ErrorCode {
    LOGIN_BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "invalid login information"),
    INVALID_USER(HttpStatus.UNAUTHORIZED, "401", "user validation failed"),
    MISMATCHED_PASSWORD(HttpStatus.UNAUTHORIZED, "401", "mismatched password"),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404", "user not found");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
