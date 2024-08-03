package com.fluffytime.join.exception;

import com.fluffytime.join.exception.errorcode.JoinErrorCode;

public class TempUserNotFound extends FluffyException {

    public TempUserNotFound() {
        super(JoinErrorCode.NOT_FOUND_TEMP_USER);
    }
}
