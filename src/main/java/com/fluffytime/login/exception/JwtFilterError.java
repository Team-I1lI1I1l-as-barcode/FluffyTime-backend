package com.fluffytime.login.exception;

import com.fluffytime.login.exception.errorcode.JwtErrorCode;

public class JwtFilterError extends FluffyException {

    public JwtFilterError() {
        super(JwtErrorCode.JWT_FILTER_ERROR);
    }
}
