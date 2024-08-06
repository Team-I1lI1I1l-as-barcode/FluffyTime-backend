package com.fluffytime.join.exception;

import com.fluffytime.join.exception.errorcode.JoinErrorCode;

public class InvalidTempUser extends FluffyException {

    public InvalidTempUser() {
        super(JoinErrorCode.INVALID_TEMP_USER);
    }
}
