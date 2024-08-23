package com.fluffytime.post.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponse<T> { //API 응답을 일관된 형식으로 반환하기 위해 사용

    private final T data;

    @Builder
    public ApiResponse(T data) {
        this.data = data;
    }

    public static <T> ApiResponse<T> response(T data) {
        return ApiResponse.<T>builder()
            .data(data)
            .build();
    }
}
