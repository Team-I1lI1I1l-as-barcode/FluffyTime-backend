package com.fluffytime.common.exception.global;

public class ProfileNotFound extends GlobalException {

    public ProfileNotFound() {
        super(GlobalErrorCode.PROFILE_NOT_FOUND);
    }
}
