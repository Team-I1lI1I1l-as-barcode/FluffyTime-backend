package com.fluffytime.common.exception.global;

public class TagNotFound extends GlobalException {

    public TagNotFound() {
        super(GlobalErrorCode.PROFILE_NOT_FOUND);
    }
}
