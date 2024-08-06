package com.fluffytime.join.dto.response;

import lombok.Getter;

@Getter
public enum JoinResponseCode {
    JOIN_SUCCESS("200", "user join successful"),
    TEMP_JOIN_SUCCESS("200", "temp user join successful"),
    NOT_DUPLICATED_NICKNAME("200", "nickname does not duplicate"),
    NOT_DUPLICATED_EMAIL("200", "Email does not duplicate"),
    SUCCESS_SEND_CERTIFICATION_EMAIL("200", "Successfully sent certification mail."),
    SUCCEED_EMAIL_CERTIFICATION("200", "Successfully certificated mail.");

    private final String code;
    private final String message;

    JoinResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
