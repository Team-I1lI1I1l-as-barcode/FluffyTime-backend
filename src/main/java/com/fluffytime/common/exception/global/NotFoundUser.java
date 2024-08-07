package com.fluffytime.common.exception.global;

public class NotFoundUser extends GlobalException {

    public NotFoundUser() {
        super(GlobalErrorCode.NOT_FOUND_USER);
    }
}
