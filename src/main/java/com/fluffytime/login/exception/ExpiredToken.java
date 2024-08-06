package com.fluffytime.login.exception;

import com.fluffytime.login.exception.errorcode.JwtErrorCode;

public class ExpiredToken extends FluffyException {

    public ExpiredToken() {
        super(JwtErrorCode.EXPIRED_TOKEN);
    }
}
