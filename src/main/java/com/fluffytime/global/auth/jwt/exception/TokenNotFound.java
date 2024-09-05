package com.fluffytime.global.auth.jwt.exception;

import com.fluffytime.global.common.exception.auth.AuthException;

public class TokenNotFound extends AuthException {

    public TokenNotFound() {
        super(JwtErrorCode.TOKEN_NOT_FOUND);
    }
}
