package com.fluffytime.common.exception.global;

public class PermissionNotAccess extends GlobalException {

    public PermissionNotAccess() {
        super(GlobalErrorCode.PERMISSION_NOT_ACCESS);
    }
}
