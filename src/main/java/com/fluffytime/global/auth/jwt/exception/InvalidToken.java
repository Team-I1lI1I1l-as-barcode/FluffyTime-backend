package com.fluffytime.global.auth.jwt.exception;

import com.fluffytime.global.common.exception.auth.AuthException;

public class InvalidToken extends AuthException {

    public InvalidToken() {
        super(JwtErrorCode.INVALID_TOKEN);
    }
}
