package com.fluffytime.user.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class LoginTypeError extends FluffyException {

    public LoginTypeError() {
        super(LoginErrorCode.LOGIN_TYPE_ERROR);
    }
}
