package com.fluffytime.comment.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class NotPermissionModify extends FluffyException {

    public NotPermissionModify() {
        super(CommentErrorCode.NOT_PERMISSION_MODIFY);
    }
}
