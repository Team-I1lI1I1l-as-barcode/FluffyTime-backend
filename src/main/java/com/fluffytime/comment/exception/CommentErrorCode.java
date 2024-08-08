package com.fluffytime.comment.exception;

import com.fluffytime.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "CE-001", "올바르지 않은 입력값입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
