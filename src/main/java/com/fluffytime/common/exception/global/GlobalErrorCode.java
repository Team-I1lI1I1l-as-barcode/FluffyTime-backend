package com.fluffytime.common.exception.global;

import com.fluffytime.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "GE-001", "유저를 찾을 수 없습니다"),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "GE-002", "게시글을 찾을 수 없습니다"),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "GE-003", "댓글을 찾을 수 없습니다"),
    NOT_FOUND_REPLY(HttpStatus.NOT_FOUND, "GE-004", "프로필을 찾을 수 없습니다"),
    NOT_FOUND_PROFILE(HttpStatus.NOT_FOUND, "GE-005", "태그를 찾을 수 없습니다"),
    NOT_FOUND_TAG(HttpStatus.NOT_FOUND, "GE-006", "답글을 찾을 수 없습니다"),
    NOT_EDIT_PERMISSION(HttpStatus.FORBIDDEN, "GE-007", "편집 권한이 없습니다"),
    NOT_ACCESS_PERMISSION(HttpStatus.FORBIDDEN, "GE-008", "접근 권한이 없습니다"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GE-009", "서버 에러"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "GE-010", "잘못된 요청"),
    NOT_FOUND_ROLE_NAME(HttpStatus.NOT_FOUND, "GE-011", "유저 권한 명을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
