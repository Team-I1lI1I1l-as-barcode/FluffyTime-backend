package com.fluffytime.global.auth.oauth2.util.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegistrationId {
    NAVER_ID("naver"),
    GOOGLE_ID("google");
    private final String id;
}
