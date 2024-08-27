package com.fluffytime.domain.user.exception;

import com.fluffytime.domain.user.exception.codes.JoinErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class TempUserNotFound extends FluffyException {

    public TempUserNotFound() {
        super(JoinErrorCode.TEMP_USER_NOT_FOUND);
    }
}
