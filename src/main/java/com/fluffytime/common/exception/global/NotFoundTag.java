package com.fluffytime.common.exception.global;

public class NotFoundTag extends GlobalException {

    public NotFoundTag() {
        super(GlobalErrorCode.TAG_NOT_FOUND);
    }
}
