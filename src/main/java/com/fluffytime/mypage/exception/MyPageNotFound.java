package com.fluffytime.mypage.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class MyPageNotFound extends FluffyException {

    public MyPageNotFound() {
        super(MyPageErrorCode.MY_PAGE_NOT_FOUND);
    }
}
