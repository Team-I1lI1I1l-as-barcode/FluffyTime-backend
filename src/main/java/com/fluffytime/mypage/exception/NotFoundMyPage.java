package com.fluffytime.mypage.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class NotFoundMyPage extends FluffyException {

    public NotFoundMyPage() {
        super(MyPageErrorCode.NOT_FOUND_MY_PAGE);
    }
}
