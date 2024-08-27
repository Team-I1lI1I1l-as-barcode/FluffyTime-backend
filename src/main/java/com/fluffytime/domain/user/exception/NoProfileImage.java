package com.fluffytime.domain.user.exception;

import com.fluffytime.domain.user.exception.codes.MyPageErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

// IllegalArgumentException 예외
public class NoProfileImage extends FluffyException {

    public NoProfileImage() {
        super(MyPageErrorCode.NO_PROFILE_IMAGE);
    }

}
