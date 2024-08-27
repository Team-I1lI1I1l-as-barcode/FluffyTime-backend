package com.fluffytime.domain.user.exception;

import com.fluffytime.domain.user.exception.codes.JoinErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class InvalidTempUser extends FluffyException {

    public InvalidTempUser() {
        super(JoinErrorCode.INVALID_TEMP_USER);
    }
}
