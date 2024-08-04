package com.fluffytime.explore.dto.response;

import lombok.Getter;

@Getter
public enum Code {
    SUCCESS("200", "Successfully loaded explore screen");

    private final String code;
    private final String message;

    Code(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
