package com.fluffytime.common.exception.global;

public class NotFoundReply extends GlobalException {

    public NotFoundReply() {
        super(GlobalErrorCode.NOT_FOUND_REPLY);
    }
}
