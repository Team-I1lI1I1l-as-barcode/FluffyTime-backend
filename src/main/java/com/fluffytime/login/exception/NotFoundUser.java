package com.fluffytime.login.exception;

import com.fluffytime.login.exception.errorcode.LoginErrorCode;

public class NotFoundUser extends FluffyException {

    public NotFoundUser() {
        super(LoginErrorCode.NOT_FOUND_USER);
    }
}
