package com.fluffytime.comment.config.error.exception;

import com.fluffytime.comment.config.error.ErrorCode;

//발생하는 예외를 모아두는 최상위 클래스
public class CommentException extends RuntimeException {

    private final ErrorCode errorCode;

    public CommentException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CommentException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
