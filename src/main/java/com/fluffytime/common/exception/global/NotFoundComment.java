package com.fluffytime.common.exception.global;

public class NotFoundComment extends GlobalException {

    public NotFoundComment() {
        super(GlobalErrorCode.NOT_FOUND_COMMENT);
    }
}
