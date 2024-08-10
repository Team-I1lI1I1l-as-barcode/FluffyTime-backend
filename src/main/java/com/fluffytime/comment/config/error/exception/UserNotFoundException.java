package com.fluffytime.comment.config.error.exception;

import com.fluffytime.comment.config.error.ErrorCode;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
