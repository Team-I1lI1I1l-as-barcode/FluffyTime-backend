package com.fluffytime.domain.user.exception;

import com.fluffytime.domain.user.exception.codes.LoginErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class MismatchedPassword extends FluffyException {

    public MismatchedPassword() {
        super(LoginErrorCode.MISMATCHED_PASSWORD);
    }
}
