package com.fluffytime.domain.board.exception;

import com.fluffytime.domain.board.exception.codes.LikeErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class LikeIsExists extends FluffyException {

    public LikeIsExists() {
        super(LikeErrorCode.LIKE_IS_EXISTS);
    }
}
