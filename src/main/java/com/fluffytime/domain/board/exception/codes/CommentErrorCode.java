package com.fluffytime.domain.board.exception.codes;

import com.fluffytime.global.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements ErrorCode {
    NOT_PERMISSION_MODIFY(HttpStatus.NOT_MODIFIED, "CE-001", "수정 권한이 없습니다."),
    NOT_PERMISSION_DELETE(HttpStatus.NOT_MODIFIED, "CE-002", "삭제 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
