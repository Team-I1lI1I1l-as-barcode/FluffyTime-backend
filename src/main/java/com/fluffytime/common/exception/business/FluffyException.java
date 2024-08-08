package com.fluffytime.common.exception.business;

import com.fluffytime.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class FluffyException extends RuntimeException {

    private final ErrorCode errorCode;

    public FluffyException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
