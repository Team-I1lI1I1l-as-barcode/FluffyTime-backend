package com.fluffytime.join.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class AlreadyExistsEmail extends FluffyException {

    public AlreadyExistsEmail() {
        super(JoinErrorCode.EXISTS_DUPLICATED_EMAIL);
    }
}
