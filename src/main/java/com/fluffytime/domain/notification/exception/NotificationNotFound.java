package com.fluffytime.domain.notification.exception;

import com.fluffytime.domain.notification.exception.codes.NotificationErrorCode;
import com.fluffytime.global.common.exception.business.FluffyException;

public class NotificationNotFound extends FluffyException {

    public NotificationNotFound() {
        super(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
    }
}
