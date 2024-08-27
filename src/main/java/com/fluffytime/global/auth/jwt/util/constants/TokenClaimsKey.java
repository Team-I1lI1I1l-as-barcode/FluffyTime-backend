package com.fluffytime.global.auth.jwt.util.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenClaimsKey {
    USER_ID("userId"),
    NICKNAME("nickname"),
    ROLES("roles");

    private final String key;
}
