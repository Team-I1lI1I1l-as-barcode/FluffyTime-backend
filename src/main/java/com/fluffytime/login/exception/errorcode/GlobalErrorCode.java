package com.fluffytime.login.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "Bad Request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "Server error");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
