package com.fluffytime.domain.board.exception;

import com.fluffytime.domain.board.exception.codes.CommentErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class NotPermissionModify extends FluffyException {

    public NotPermissionModify() {
        super(CommentErrorCode.NOT_PERMISSION_MODIFY);
    }
}
