package com.fluffytime.join.dto.reponse;

import lombok.Getter;

@Getter
public enum ResponseCode {
    JOIN_SUCCESS("200", "user join successful"),
    TEMP_JOIN_SUCCESS("200", "temp user join successful"),
    NOT_DUPLICATED_NICKNAME("200", "nickname does not duplicate"),
    NOT_DUPLICATED_EMAIL("200", "Email does not duplicate"),
    SUCCESS_SEND_CERTIFICATION_EMAIL("200", "Successfully sent certification mail.");

    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
