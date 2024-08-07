package com.fluffytime.login.exception.jwt;

import com.fluffytime.common.exception.business.FluffyException;

public class NotFoundToken extends FluffyException {

    public NotFoundToken() {
        super(JwtErrorCode.NOT_FOUND_TOKEN);
    }
}
