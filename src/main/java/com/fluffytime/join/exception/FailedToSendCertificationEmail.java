package com.fluffytime.join.exception;

import com.fluffytime.join.exception.errorcode.JoinErrorCode;

public class FailedToSendCertificationEmail extends FluffyException {

    public FailedToSendCertificationEmail() {
        super(JoinErrorCode.FAILED_TO_SEND_CERTIFICATION_EMAIL);
    }
}
