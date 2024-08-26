package com.fluffytime.common.exception.global;

public class FailedToSendEmail extends GlobalException {

    public FailedToSendEmail() {
        super(GlobalErrorCode.FAILED_TO_SEND_EMAIL);
    }
}
