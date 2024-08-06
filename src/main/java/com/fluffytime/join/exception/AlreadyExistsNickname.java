package com.fluffytime.join.exception;

import com.fluffytime.join.exception.errorcode.JoinErrorCode;

public class AlreadyExistsNickname extends FluffyException {

    public AlreadyExistsNickname() {
        super(JoinErrorCode.EXISTS_DUPLICATED_NICKNAME);
    }
}
