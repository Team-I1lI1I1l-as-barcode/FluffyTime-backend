package com.fluffytime.login.exception.login;

import com.fluffytime.common.exception.business.FluffyException;

public class MisMatchedPassword extends FluffyException {

    public MisMatchedPassword() {
        super(LoginErrorCode.MISMATCHED_PASSWORD);
    }
}
