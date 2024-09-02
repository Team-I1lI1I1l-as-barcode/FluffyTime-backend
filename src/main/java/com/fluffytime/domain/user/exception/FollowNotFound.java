package com.fluffytime.domain.user.exception;

import com.fluffytime.domain.user.exception.codes.FollowErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class FollowNotFound extends FluffyException {

    public FollowNotFound() {
        super(FollowErrorCode.FOLLOW_NOT_FOUND);
    }
}
