package com.fluffytime.domain.user.exception;

import com.fluffytime.domain.user.exception.codes.UserPageErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class UserPageNotFound extends FluffyException {

    public UserPageNotFound() {
        super(UserPageErrorCode.USER_PAGE_NOT_FOUND);
    }
}
