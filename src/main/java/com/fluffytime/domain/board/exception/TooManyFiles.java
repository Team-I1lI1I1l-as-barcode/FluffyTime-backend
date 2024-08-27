package com.fluffytime.domain.board.exception;

import com.fluffytime.domain.board.exception.codes.PostErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class TooManyFiles extends FluffyException {

    public TooManyFiles() {
        super(PostErrorCode.TOO_MANY_FILES);
    }
}
