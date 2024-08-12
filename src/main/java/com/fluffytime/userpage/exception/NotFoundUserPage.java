package com.fluffytime.userpage.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class NotFoundUserPage extends FluffyException {

    public NotFoundUserPage() {
        super(UserPageErrorCode.NOT_FOUND_USER_PAGE);
    }
}
