package com.fluffytime.comment.exception;

import com.fluffytime.common.exception.business.FluffyException;

public class NotPermissionDelete extends FluffyException {

    public NotPermissionDelete() {
        super(CommentErrorCode.NOT_PERMISSION_DELETE);
    }
}
