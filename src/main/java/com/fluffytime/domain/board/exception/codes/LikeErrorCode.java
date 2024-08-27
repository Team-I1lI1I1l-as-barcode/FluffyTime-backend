package com.fluffytime.domain.board.exception.codes;

import com.fluffytime.global.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LikeErrorCode implements ErrorCode {
    LIKE_IS_EXISTS(HttpStatus.BAD_REQUEST, "LE-001", "좋아요가 이미 존재합니다."),
    NO_LIKE_FOUND(HttpStatus.BAD_REQUEST, "LE-002", "좋아요가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private String message;
}
