package com.fluffytime.join.exception;

import com.fluffytime.join.exception.errorcode.JoinErrorCode;

public class NotFoundTempUser extends FluffyException {

    public NotFoundTempUser() {
        super(JoinErrorCode.NOT_FOUND_TEMP_USER);
    }
}
