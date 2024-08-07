package com.fluffytime.login.exception.jwt;

import com.fluffytime.common.exception.business.FluffyException;

public class JwtFilterError extends FluffyException {

    public JwtFilterError() {
        super(JwtErrorCode.JWT_FILTER_ERROR);
    }
}
