package com.fluffytime.post.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class PostNotInTempStatus extends FluffyException {

    public PostNotInTempStatus() {
        super(PostErrorCode.POST_NOT_IN_TEMP_STATUS);
    }
}
