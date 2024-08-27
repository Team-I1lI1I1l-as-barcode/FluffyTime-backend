package com.fluffytime.domain.board.exception;

import com.fluffytime.domain.board.exception.codes.PostErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class UnsupportedFileFormat extends FluffyException {

    public UnsupportedFileFormat() {
        super(PostErrorCode.UNSUPPORTED_FILE_FORMAT);
    }
}
