package com.fluffytime.comment.exception;

import com.fluffytime.common.exception.business.FluffyException;
import com.fluffytime.common.exception.global.GlobalErrorCode;

public class CommentNotFoundException extends FluffyException {

    public CommentNotFoundException() {
        super(GlobalErrorCode.NOT_FOUND_COMMENT);
    }
}
