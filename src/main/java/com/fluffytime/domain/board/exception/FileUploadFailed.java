package com.fluffytime.domain.board.exception;

import com.fluffytime.domain.board.exception.codes.PostErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class FileUploadFailed extends FluffyException {

    public FileUploadFailed() {
        super(PostErrorCode.FILE_UPLOAD_FAILED);
    }
}
