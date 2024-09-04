package com.fluffytime.domain.board.exception.codes;

import com.fluffytime.global.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BookmarkErrorCode implements ErrorCode {
    BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "BE-001", "이미 이 게시물에 대한 북마크가 존재합니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BE-002", "북마크를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
