package com.fluffytime.join.exception;

import com.fluffytime.join.exception.errorcode.JoinErrorCode;

public class RoleNameNotFound extends FluffyException {

    public RoleNameNotFound() {
        super(JoinErrorCode.NOT_FOUND_ROLE_NAME);
    }
}
