package com.fluffytime.join.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class NotFoundTempUser extends FluffyException {

    public NotFoundTempUser() {
        super(JoinErrorCode.NOT_FOUND_TEMP_USER);
    }
}
