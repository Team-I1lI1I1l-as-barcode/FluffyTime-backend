package com.fluffytime.post.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class ContentLengthExceeded extends FluffyException {

    public ContentLengthExceeded() {
        super(PostErrorCode.CONTENT_LENGTH_EXCEEDED);
    }
}
