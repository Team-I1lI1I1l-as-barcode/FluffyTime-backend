package com.fluffytime.login.exception;

import com.fluffytime.login.exception.errorcode.ErrorCode;
import com.fluffytime.login.exception.errorcode.JwtErrorCode;

public class UnknownError extends FluffyException {

    public UnknownError() {
        super(JwtErrorCode.UNKNOWN_ERROR);
    }
}
