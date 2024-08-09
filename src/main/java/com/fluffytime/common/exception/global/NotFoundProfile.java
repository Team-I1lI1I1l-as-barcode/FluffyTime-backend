package com.fluffytime.common.exception.global;

public class NotFoundProfile extends GlobalException {

    public NotFoundProfile() {
        super(GlobalErrorCode.NOT_FOUND_PROFILE);
    }
}
