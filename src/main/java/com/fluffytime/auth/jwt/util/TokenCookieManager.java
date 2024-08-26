package com.fluffytime.auth.jwt.util;

import static com.fluffytime.auth.jwt.util.constants.TokenExpiry.REMOVE_EXPIRY;

import jakarta.servlet.http.Cookie;

public class TokenCookieManager {
    public static final int TOKEN_EXPIRY = 0;

    public static Cookie generateTokenCookie(String name, String value, Long expiry) {
        Cookie tokenCookie = new Cookie(name, value);
        tokenCookie.setPath("/");
        tokenCookie.setHttpOnly(true);
        tokenCookie.setMaxAge(
            Math.toIntExact(expiry));
        return tokenCookie;
    }

    public static Cookie removeCookie(String name) {
        Cookie Cookie = new Cookie(name, null);
        Cookie.setMaxAge(Math.toIntExact(REMOVE_EXPIRY.getExpiry()));
        Cookie.setPath("/");
        return Cookie;
    }
}
