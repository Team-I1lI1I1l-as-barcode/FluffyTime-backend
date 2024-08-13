package com.fluffytime.mypage.exception;

import com.fluffytime.common.exception.business.FluffyException;

// IllegalArgumentException 예외
public class NoProfileImage extends FluffyException {

    public NoProfileImage() {
        super(MyPageErrorCode.NO_PROFILE_IMAGE);
    }

}
