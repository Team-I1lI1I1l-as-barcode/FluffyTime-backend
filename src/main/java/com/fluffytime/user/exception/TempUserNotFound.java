package com.fluffytime.user.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class TempUserNotFound extends FluffyException {

    public TempUserNotFound() {
        super(JoinErrorCode.TEMP_USER_NOT_FOUND);
    }
}
