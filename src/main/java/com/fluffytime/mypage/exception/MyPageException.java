package com.fluffytime.mypage.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MyPageException extends RuntimeException {

    private final String code;

    public MyPageException(String code, String message) {
        super(message);
        this.code = code;
    }

}
