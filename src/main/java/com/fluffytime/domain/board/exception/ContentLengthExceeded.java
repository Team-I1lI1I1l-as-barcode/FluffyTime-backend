package com.fluffytime.domain.board.exception;

import com.fluffytime.domain.board.exception.codes.PostErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class ContentLengthExceeded extends FluffyException {

    public ContentLengthExceeded() {
        super(PostErrorCode.CONTENT_LENGTH_EXCEEDED);
    }
}
