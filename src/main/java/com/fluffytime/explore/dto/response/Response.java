package com.fluffytime.explore.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Response<T> {

    private final String code;
    private final String message;
    private final T data;

    @Builder
    public Response(Code responseCode, T data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
    }

    public static <T> Response<T> response(Code responseCode) {
        return response(responseCode, null);
    }

    public static <T> Response<T> response(Code responseCode, T data) {
        return Response.<T>builder()
            .responseCode(responseCode)
            .data(data)
            .build();
    }
}
