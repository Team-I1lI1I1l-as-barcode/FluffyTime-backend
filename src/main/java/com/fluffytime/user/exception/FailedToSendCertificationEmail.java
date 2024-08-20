package com.fluffytime.user.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class FailedToSendCertificationEmail extends FluffyException {

    public FailedToSendCertificationEmail() {
        super(JoinErrorCode.FAILED_TO_SEND_CERTIFICATION_EMAIL);
    }
}
