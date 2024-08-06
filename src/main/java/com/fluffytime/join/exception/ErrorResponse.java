package com.fluffytime.join.exception;

import com.fluffytime.join.exception.errorcode.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String code;
    private String message;

    private ErrorResponse(ErrorCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
    }

    private ErrorResponse(ErrorCode code, String message) {
        this.code = code.getCode();
        this.message = message;
    }

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(ErrorCode code, String message) {
        return new ErrorResponse(code, message);
    }
}
