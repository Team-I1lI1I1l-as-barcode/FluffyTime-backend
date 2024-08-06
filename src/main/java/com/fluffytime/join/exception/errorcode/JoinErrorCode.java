package com.fluffytime.join.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JoinErrorCode implements ErrorCode {
    JOIN_BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "invalid membership information"),
    EXISTS_DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "400", "duplicate email found"),
    EXISTS_DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "400", "duplicate nickname found"),
    NOT_FOUND_TEMP_USER(HttpStatus.NOT_FOUND, "404", "temp user not found"),
    INVALID_TEMP_USER(HttpStatus.UNAUTHORIZED, "401", "invalid temp user"),
    NOT_FOUND_ROLE_NAME(HttpStatus.NOT_FOUND, "404", "role name not found"),
    FAILED_TO_SEND_CERTIFICATION_EMAIL(HttpStatus.BAD_REQUEST, "400",
        "failed to send certification email");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
