package com.fluffytime.post.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponse<T> { //API 응답을 일관된 형식으로 반환하기 위해 사용

    private final String code;
    private final String message;
    private final T data;

    @Builder
    public ApiResponse(PostResponseCode responseCode, T data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
    }

    public static <T> ApiResponse<T> response(PostResponseCode responseCode) {
        return response(responseCode, null);
    }

    public static <T> ApiResponse<T> response(PostResponseCode responseCode, T data) {
        return ApiResponse.<T>builder()
            .responseCode(responseCode)
            .data(data)
            .build();
    }
}
