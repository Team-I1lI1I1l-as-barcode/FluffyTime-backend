package com.fluffytime.domain.user.exception.codes;

import com.fluffytime.global.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FollowErrorCode implements ErrorCode {
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "FE-001", "팔로우 관계를 찾아낼 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
