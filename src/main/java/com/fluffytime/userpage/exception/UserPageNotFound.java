package com.fluffytime.userpage.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class UserPageNotFound extends FluffyException {

    public UserPageNotFound() {
        super(UserPageErrorCode.USER_PAGE_NOT_FOUND);
    }
}
