package com.fluffytime.post.exception;

import com.fluffytime.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements ErrorCode {
    TOO_MANY_FILES(HttpStatus.BAD_REQUEST, "PE-001", "최대 10개의 이미지만 업로드할 수 있습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PE-002", "파일 업로드 실패했습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "PE-003", "파일 크기가 허용 범위를 초과했습니다."),
    UNSUPPORTED_FILE_FORMAT(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "PE-004", "지원하지 않는 파일 형식입니다."),
    CONTENT_LENGTH_EXCEEDED(HttpStatus.BAD_REQUEST, "PE-005", "본문 길이가 2200자를 초과했습니다."),
    POST_NOT_IN_TEMP_STATUS(HttpStatus.BAD_REQUEST, "PE-006", "게시글이 임시저장 상태가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
