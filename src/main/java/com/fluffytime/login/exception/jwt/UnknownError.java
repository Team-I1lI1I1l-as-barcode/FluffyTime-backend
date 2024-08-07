package com.fluffytime.login.exception.jwt;

import com.fluffytime.common.exception.business.FluffyException;

public class UnknownError extends FluffyException {

    public UnknownError() {
        super(JwtErrorCode.UNKNOWN_ERROR);
    }
}
