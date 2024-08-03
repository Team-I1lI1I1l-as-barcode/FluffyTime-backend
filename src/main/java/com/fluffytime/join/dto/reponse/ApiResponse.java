package com.fluffytime.join.dto.reponse;


import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final String code;
    private final String message;
    private final T data;

    @Builder
    public ApiResponse(ResponseCode responseCode, T data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
    }

    public static <T> ApiResponse<T> response(ResponseCode responseCode) {
        return response(responseCode, null);
    }

    public static <T> ApiResponse<T> response(ResponseCode responseCode, T data) {
        return ApiResponse.<T>builder()
            .responseCode(responseCode)
            .data(data)
            .build();
    }
}
