package com.fluffytime.login.exception;

import com.fluffytime.login.exception.errorcode.JwtErrorCode;

public class InvalidToken extends FluffyException {

    public InvalidToken() {
        super(JwtErrorCode.INVALID_TOKEN);
    }
}
