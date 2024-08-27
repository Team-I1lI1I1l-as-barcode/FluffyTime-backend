package com.fluffytime.domain.user.exception;

import com.fluffytime.domain.user.exception.codes.MyPageErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class MyPageNotFound extends FluffyException {

    public MyPageNotFound() {
        super(MyPageErrorCode.MY_PAGE_NOT_FOUND);
    }
}
