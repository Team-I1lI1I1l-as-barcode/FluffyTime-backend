package com.fluffytime.common.exception.global;

public class UserNotFound extends GlobalException {

    public UserNotFound() {
        super(GlobalErrorCode.USER_NOT_FOUND);
    }
}
