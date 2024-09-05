package com.fluffytime.global.auth.jwt.util.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenName {
    REFRESH_TOKEN_NAME("refreshToken"),
    ACCESS_TOKEN_NAME("accessToken");

    private final String name;
}
