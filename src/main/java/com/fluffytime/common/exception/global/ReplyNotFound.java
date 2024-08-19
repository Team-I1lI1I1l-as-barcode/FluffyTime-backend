package com.fluffytime.common.exception.global;

public class ReplyNotFound extends GlobalException {

    public ReplyNotFound() {
        super(GlobalErrorCode.REPLY_NOT_FOUND);
    }
}
