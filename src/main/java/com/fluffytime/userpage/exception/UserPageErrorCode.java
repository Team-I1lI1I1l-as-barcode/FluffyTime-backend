package com.fluffytime.userpage.exception;

import com.fluffytime.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserPageErrorCode implements ErrorCode {
    NOT_FOUND_USER_PAGE(HttpStatus.NOT_FOUND, "UE-001", "유저페이지를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
