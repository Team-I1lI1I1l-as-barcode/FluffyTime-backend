package com.fluffytime.login.exception;

import com.fluffytime.login.exception.errorcode.JwtErrorCode;

public class NotFoundToken extends FluffyException {

    public NotFoundToken() {
        super(JwtErrorCode.NOT_FOUND_TOKEN);
    }
}
