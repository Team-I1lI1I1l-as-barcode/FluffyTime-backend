package com.fluffytime.login.exception;

import com.fluffytime.login.exception.errorcode.LoginErrorCode;

public class MisMatchedPassword extends FluffyException {

    public MisMatchedPassword() {
        super(LoginErrorCode.MISMATCHED_PASSWORD);
    }
}
