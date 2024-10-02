package com.fluffytime.domain.notification.util.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminNotificationMessage {
    JOIN_NOTICE("[ %s ]\n유저가 가입하였습니다."),
    WITHDRAW_NOTICE("[ %s ]\n유저가 계정을 삭제하였습니다."),
    REG_POST_NOTICE("[ %s ]\n게시물 ID : %d번\n게시물을 등록하였습니다."),
    DELETE_POST_NOTICE("[ %s ]\n게시물 ID : %d번\n게시물을 삭제하였습니다.");


    private final String message;

    public String joinNotice(String name) {
        return String.format(message,name);
    }

    public String withdrawNotice(String name) {
        return String.format(message,name);
    }

    public String regPostNotice(String name, Long postId) {
        return String.format(message, name, postId);
    }

    public String deletePostNotice(String name, Long postId) {
        return String.format(message, name, postId);
    }
}
