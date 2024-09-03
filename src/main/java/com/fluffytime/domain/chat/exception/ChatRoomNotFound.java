package com.fluffytime.domain.chat.exception;

import com.fluffytime.global.common.exception.business.FluffyException;

public class ChatRoomNotFound extends FluffyException {

    public ChatRoomNotFound() {
        super(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
    }
}
