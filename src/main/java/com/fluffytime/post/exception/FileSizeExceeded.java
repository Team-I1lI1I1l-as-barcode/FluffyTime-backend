package com.fluffytime.post.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class FileSizeExceeded extends FluffyException {

    public FileSizeExceeded() {
        super(PostErrorCode.FILE_SIZE_EXCEEDED);
    }
}
