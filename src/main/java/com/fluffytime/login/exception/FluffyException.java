package com.fluffytime.login.exception;

import com.fluffytime.login.exception.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public class FluffyException extends RuntimeException {

    private final ErrorCode errorCode;

    public FluffyException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FluffyException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
