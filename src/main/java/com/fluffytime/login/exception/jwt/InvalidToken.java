package com.fluffytime.login.exception.jwt;

import com.fluffytime.common.exception.business.FluffyException;

public class InvalidToken extends FluffyException {

    public InvalidToken() {
        super(JwtErrorCode.INVALID_TOKEN);
    }
}
