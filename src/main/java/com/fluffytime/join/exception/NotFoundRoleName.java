package com.fluffytime.join.exception;

import com.fluffytime.join.exception.errorcode.JoinErrorCode;

public class NotFoundRoleName extends FluffyException {

    public NotFoundRoleName() {
        super(JoinErrorCode.NOT_FOUND_ROLE_NAME);
    }
}
