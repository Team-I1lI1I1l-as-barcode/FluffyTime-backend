package com.fluffytime.join.reponse;


import com.fluffytime.join.common.ResponseCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseDto<T> {

    private final int code;
    private final String message;
    private final T data;

    @Builder
    public ResponseDto(ResponseCode responseCode, T data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
    }

    public static <T> ResponseDto<T> response(ResponseCode responseCode) {
        return response(responseCode, null);
    }

    public static <T> ResponseDto<T> response(ResponseCode responseCode, T data) {
        return ResponseDto.<T>builder()
            .responseCode(responseCode)
            .data(data)
            .build();
    }
}
