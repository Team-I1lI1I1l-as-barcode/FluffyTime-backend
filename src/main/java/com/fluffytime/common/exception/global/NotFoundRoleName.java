package com.fluffytime.common.exception.global;

public class NotFoundRoleName extends GlobalException {

    public NotFoundRoleName() {
        super(GlobalErrorCode.NOT_FOUND_ROLE_NAME);
    }
}
