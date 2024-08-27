package com.fluffytime.domain.board.exception;

import com.fluffytime.domain.board.exception.codes.LikeErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class NoLikeFound extends FluffyException {

    public NoLikeFound() {
        super(LikeErrorCode.NO_LIKE_FOUND);
    }
}
