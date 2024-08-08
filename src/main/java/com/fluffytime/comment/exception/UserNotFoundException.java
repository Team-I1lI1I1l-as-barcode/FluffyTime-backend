package com.fluffytime.comment.exception;

import com.fluffytime.common.exception.business.FluffyException;
import com.fluffytime.common.exception.global.GlobalErrorCode;

public class UserNotFoundException extends FluffyException {

    public UserNotFoundException() {
        super(GlobalErrorCode.NOT_FOUND_USER);
    }
}
