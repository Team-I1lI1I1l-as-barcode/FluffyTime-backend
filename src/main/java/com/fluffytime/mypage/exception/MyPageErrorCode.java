package com.fluffytime.mypage.exception;

import com.fluffytime.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MyPageErrorCode implements ErrorCode {
    NOT_FOUND_MY_PAGE(HttpStatus.NOT_FOUND, "ME-001", "마이페이지를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
