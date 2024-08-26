package com.fluffytime.like.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class NoLikeFound extends FluffyException {

    public NoLikeFound() {
        super(LikeErrorCode.NO_LIKE_FOUND);
    }
}
