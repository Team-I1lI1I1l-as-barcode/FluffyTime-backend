package com.fluffytime.auth.jwt.util.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenExpiry {
    REFRESH_TOKEN_EXPIRY(7 * 24 * 60 * 60 * 1000L),
    REFRESH_TOKEN_EXPIRY_SECOND(7 * 24 * 60 * 60L),
    ACCESS_TOKEN_EXPIRY(60 * 60 * 1000L),
    ACCESS_TOKEN_EXPIRY_SECOND(60 * 60L),
    REMOVE_EXPIRY(0L);

    private final Long expiry;
}
