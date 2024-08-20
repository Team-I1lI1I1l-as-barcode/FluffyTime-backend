package com.fluffytime.post.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class TooManyFiles extends FluffyException {

    public TooManyFiles() {
        super(PostErrorCode.TOO_MANY_FILES);
    }
}
