package com.fluffytime.myPage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponseDto {

    private final String code;
    private final String message;

    public static ErrorResponseDto of(String code, String message) {
        return new ErrorResponseDto(code, message);
    }

}
