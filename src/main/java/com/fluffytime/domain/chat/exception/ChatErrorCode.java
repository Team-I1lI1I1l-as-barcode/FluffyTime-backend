package com.fluffytime.domain.chat.exception;

import com.fluffytime.global.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "DM-001", "메시지방이 없습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
