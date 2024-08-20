package com.fluffytime.auth.jwt.exception;

import com.fluffytime.common.exception.auth.AuthException;

public class TokenNotFound extends AuthException {

    public TokenNotFound() {
        super(JwtErrorCode.TOKEN_NOT_FOUND);
    }
}
