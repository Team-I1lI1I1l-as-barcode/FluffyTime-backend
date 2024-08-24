package com.fluffytime.like.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ErrorDto {

    private final String code;
    private final String message;

    public static ErrorDto getErrorDto(String code, String message) {
        return new ErrorDto(code, message);
    }
}
