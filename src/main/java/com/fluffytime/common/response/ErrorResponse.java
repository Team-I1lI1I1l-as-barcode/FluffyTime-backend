package com.fluffytime.common.response;

import com.fluffytime.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;

    private ErrorResponse(ErrorCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
    }

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code);
    }
}
