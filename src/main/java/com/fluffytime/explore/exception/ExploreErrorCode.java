package com.fluffytime.explore.exception;

import com.fluffytime.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExploreErrorCode implements ErrorCode {


    //    EXISTS_DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "JE-001", "중복된 이메일이 존재합니다."),
//    EXISTS_DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "JE-002", "중복된 유저명이 존재합니다."),
//    NOT_FOUND_TEMP_USER(HttpStatus.NOT_FOUND, "JE-003", "임시유저를 찾을 수 없습니다."),
//    INVALID_TEMP_USER(HttpStatus.UNAUTHORIZED, "JE-004", "인증되지 않은 임시유저"),
    FAILED_TO_SEND_CERTIFICATION_EMAIL(HttpStatus.BAD_REQUEST, "JE-005",
        "인증 메일 전송 실패");//동작을 위해 임시로 주석 풀었음

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
