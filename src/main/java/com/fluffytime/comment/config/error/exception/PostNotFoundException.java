package com.fluffytime.comment.config.error.exception;

import com.fluffytime.comment.config.error.ErrorCode;

public class PostNotFoundException extends NotFoundException {

    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND);
    }
}
