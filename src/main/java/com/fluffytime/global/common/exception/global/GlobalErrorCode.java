package com.fluffytime.global.common.exception.global;

import com.fluffytime.global.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "GE-001", "유저를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "GE-002", "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "GE-003", "댓글을 찾을 수 없습니다."),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "GE-004", "답글을 찾을 수 없습니다."),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "GE-006", "태그를 찾을 수 없습니다."),
    PERMISSION_NOT_EDIT(HttpStatus.FORBIDDEN, "GE-007", "편집 권한이 없습니다."),
    PERMISSION_NOT_ACCESS(HttpStatus.FORBIDDEN, "GE-008", "접근 권한이 없습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GE-009", "서버 에러"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "GE-010", "잘못된 요청"),
    ROLE_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, "GE-011", "유저 권한 명을 찾을 수 없습니다"),
    FAILED_TO_SEND_EMAIL(HttpStatus.BAD_REQUEST, "GE-012", "인증 메일 전송 실패");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
