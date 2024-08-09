package com.fluffytime.login.exception.jwt;

import com.fluffytime.common.exception.business.FluffyException;

public class UnsupportedToken extends FluffyException {

    public UnsupportedToken() {
        super(JwtErrorCode.UNSUPPORTED_TOKEN);
    }
}
