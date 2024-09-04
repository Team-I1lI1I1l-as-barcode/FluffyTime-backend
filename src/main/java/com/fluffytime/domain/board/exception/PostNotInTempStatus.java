package com.fluffytime.domain.board.exception;

import com.fluffytime.domain.board.exception.codes.PostErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class PostNotInTempStatus extends FluffyException {

    public PostNotInTempStatus() {
        super(PostErrorCode.POST_NOT_IN_TEMP_STATUS);
    }
}
