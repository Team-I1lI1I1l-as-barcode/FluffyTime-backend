package com.fluffytime.common.exception.global;

public class RoleNameNotFound extends GlobalException {

    public RoleNameNotFound() {
        super(GlobalErrorCode.ROLE_NAME_NOT_FOUND);
    }
}
