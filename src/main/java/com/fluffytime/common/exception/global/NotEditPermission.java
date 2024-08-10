package com.fluffytime.common.exception.global;

public class NotEditPermission extends GlobalException {

    public NotEditPermission() {
        super(GlobalErrorCode.NOT_EDIT_PERMISSION);
    }
}
