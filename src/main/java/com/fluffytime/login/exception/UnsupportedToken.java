package com.fluffytime.login.exception;

import com.fluffytime.login.exception.errorcode.JwtErrorCode;

public class UnsupportedToken extends FluffyException {

    public UnsupportedToken() {
        super(JwtErrorCode.UNSUPPORTED_TOKEN);
    }
}
