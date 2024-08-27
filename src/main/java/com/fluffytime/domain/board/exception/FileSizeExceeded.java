package com.fluffytime.domain.board.exception;

import com.fluffytime.domain.board.exception.codes.PostErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class FileSizeExceeded extends FluffyException {

    public FileSizeExceeded() {
        super(PostErrorCode.FILE_SIZE_EXCEEDED);
    }
}
