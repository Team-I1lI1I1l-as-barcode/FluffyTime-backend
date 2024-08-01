package com.fluffytime.join.common;

import lombok.Getter;

@Getter
public enum ResponseCode {
    JOIN_SUCCESS(200, "user join successful"),
    NICKNAME_DUPLICATE_TRUE(200, "Duplicate email found"),
    NICKNAME_DUPLICATE_FALSE(200, "nickname does not exist"),
    EMAIL_DUPLICATE_TRUE(200, "Duplicate email found"),
    EMAIL_DUPLICATE_FALSE(200, "Email does not exist"),
    JOIN_BAD_REQUEST(400, "Email, password, and nickname are required");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
