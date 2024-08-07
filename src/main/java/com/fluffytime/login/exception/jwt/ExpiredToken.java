package com.fluffytime.login.exception.jwt;

import com.fluffytime.common.exception.business.FluffyException;

public class ExpiredToken extends FluffyException {

    public ExpiredToken() {
        super(JwtErrorCode.EXPIRED_TOKEN);
    }
}
