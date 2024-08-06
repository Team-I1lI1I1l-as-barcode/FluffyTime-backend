package com.fluffytime.login.dto.response;

import lombok.Getter;

@Getter
public enum LoginResponseCode {
    LOGIN_SUCCESS("200", "user login success"),
    REFRESH_TOKEN_GENERATE_SUCCESS("200", "Refresh token generation successful");

    private final String code;
    private final String message;

    LoginResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
