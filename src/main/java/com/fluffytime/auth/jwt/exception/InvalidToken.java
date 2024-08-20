package com.fluffytime.auth.jwt.exception;

import com.fluffytime.common.exception.auth.AuthException;

public class InvalidToken extends AuthException {

    public InvalidToken() {
        super(JwtErrorCode.INVALID_TOKEN);
    }
}
