package com.fluffytime.post.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class UnsupportedFileFormat extends FluffyException {

    public UnsupportedFileFormat() {
        super(PostErrorCode.UNSUPPORTED_FILE_FORMAT);
    }
}
