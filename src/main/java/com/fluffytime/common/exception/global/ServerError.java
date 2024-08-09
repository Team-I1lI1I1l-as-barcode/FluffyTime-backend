package com.fluffytime.common.exception.global;

public class ServerError extends GlobalException {

    public ServerError() {
        super(GlobalErrorCode.SERVER_ERROR);
    }
}
