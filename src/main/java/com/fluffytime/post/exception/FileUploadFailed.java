package com.fluffytime.post.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class FileUploadFailed extends FluffyException {

    public FileUploadFailed() {
        super(PostErrorCode.FILE_UPLOAD_FAILED);
    }
}
