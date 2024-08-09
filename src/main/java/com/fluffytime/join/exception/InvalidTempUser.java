package com.fluffytime.join.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class InvalidTempUser extends FluffyException {

    public InvalidTempUser() {
        super(JoinErrorCode.INVALID_TEMP_USER);
    }
}
