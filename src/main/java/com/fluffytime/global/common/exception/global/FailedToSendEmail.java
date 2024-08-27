package com.fluffytime.global.common.exception.global;

public class FailedToSendEmail extends GlobalException {

    public FailedToSendEmail() {
        super(GlobalErrorCode.FAILED_TO_SEND_EMAIL);
    }
}
