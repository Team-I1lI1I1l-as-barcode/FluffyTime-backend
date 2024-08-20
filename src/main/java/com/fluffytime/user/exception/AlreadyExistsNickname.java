package com.fluffytime.user.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class AlreadyExistsNickname extends FluffyException {

    public AlreadyExistsNickname() {
        super(JoinErrorCode.EXISTS_DUPLICATED_NICKNAME);
    }
}
