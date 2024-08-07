package com.fluffytime.common.exception.global;

public class NotFoundPost extends GlobalException {

    public NotFoundPost() {
        super(GlobalErrorCode.NOT_FOUND_POST);
    }
}
