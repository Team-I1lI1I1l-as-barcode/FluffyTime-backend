package com.fluffytime.common.exception.global;

public class NotAccessPermission extends GlobalException {

    public NotAccessPermission() {
        super(GlobalErrorCode.NOT_ACCESS_PERMISSION);
    }
}
