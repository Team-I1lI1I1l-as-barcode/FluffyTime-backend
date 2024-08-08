package com.fluffytime.comment.exception;

import com.fluffytime.common.exception.business.FluffyException;
import com.fluffytime.common.exception.global.GlobalErrorCode;

public class PostNotFoundException extends FluffyException {

    public PostNotFoundException() {
        super(GlobalErrorCode.NOT_FOUND_POST);
    }
}
