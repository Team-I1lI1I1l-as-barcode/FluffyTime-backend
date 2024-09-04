package com.fluffytime.domain.user.exception;

import com.fluffytime.domain.user.exception.codes.JoinErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class AlreadyExistsNickname extends FluffyException {

    public AlreadyExistsNickname() {
        super(JoinErrorCode.EXISTS_DUPLICATED_NICKNAME);
    }
}
