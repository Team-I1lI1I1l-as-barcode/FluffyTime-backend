package com.fluffytime.like.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class LikeIsExists extends FluffyException {

    public LikeIsExists() {
        super(LikeErrorCode.LIKE_IS_EXISTS);
    }
}
