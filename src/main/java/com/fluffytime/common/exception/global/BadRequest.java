package com.fluffytime.common.exception.global;

public class BadRequest extends GlobalException {

    public BadRequest() {
        super(GlobalErrorCode.BAD_REQUEST);
    }
}
