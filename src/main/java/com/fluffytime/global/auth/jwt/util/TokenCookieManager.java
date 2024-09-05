package com.fluffytime.global.auth.jwt.util;

import com.fluffytime.global.auth.jwt.util.constants.TokenExpiry;
import jakarta.servlet.http.Cookie;

public class TokenCookieManager {
    public static final int TOKEN_EXPIRY = 0;

    // 토큰 쿠기 생성
    public static Cookie generateTokenCookie(String name, String value, Long expiry) {
        Cookie tokenCookie = new Cookie(name, value);
        tokenCookie.setPath("/");
        tokenCookie.setHttpOnly(true);
        tokenCookie.setMaxAge(
            Math.toIntExact(expiry));
        return tokenCookie;
    }

    // 쿠키 제거
    public static Cookie removeCookie(String name) {
        Cookie Cookie = new Cookie(name, null);
        Cookie.setMaxAge(Math.toIntExact(TokenExpiry.REMOVE_EXPIRY.getExpiry()));
        Cookie.setPath("/");
        return Cookie;
    }
}
