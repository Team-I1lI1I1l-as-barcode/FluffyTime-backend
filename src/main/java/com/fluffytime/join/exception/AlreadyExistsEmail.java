package com.fluffytime.join.exception;

import com.fluffytime.join.exception.errorcode.JoinErrorCode;

public class AlreadyExistsEmail extends FluffyException {

    public AlreadyExistsEmail() {
        super(JoinErrorCode.EXISTS_DUPLICATED_EMAIL);
    }
}
