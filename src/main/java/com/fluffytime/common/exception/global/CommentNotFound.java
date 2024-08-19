package com.fluffytime.common.exception.global;

public class CommentNotFound extends GlobalException {

    public CommentNotFound() {
        super(GlobalErrorCode.COMMENT_NOT_FOUND);
    }
}
