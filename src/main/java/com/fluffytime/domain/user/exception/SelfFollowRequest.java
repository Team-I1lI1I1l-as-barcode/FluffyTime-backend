package com.fluffytime.domain.user.exception;

import com.fluffytime.domain.user.exception.codes.FollowErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class SelfFollowRequest extends FluffyException {

    public SelfFollowRequest() {
        super(FollowErrorCode.SELF_FOLLOW_REQUEST);
    }

}
