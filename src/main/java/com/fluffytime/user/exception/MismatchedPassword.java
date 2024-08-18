package com.fluffytime.user.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class MismatchedPassword extends FluffyException {

    public MismatchedPassword() {
        super(LoginErrorCode.MISMATCHED_PASSWORD);
    }
}
