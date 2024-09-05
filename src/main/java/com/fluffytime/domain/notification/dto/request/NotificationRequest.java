package com.fluffytime.domain.notification.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationRequest {

    private String message;
    private boolean isRead;
    private String type;
    private Long userId;
    private Long postId;
    private Long commentId;
    private Long replyId;

    @Builder
    public NotificationRequest(String message, boolean isRead, String type, Long userId,
        Long postId, Long commentId, Long replyId) {
        this.message = message;
        this.isRead = isRead;
        this.type = type;
        this.userId = userId;
        this.postId = postId;
        this.commentId = commentId;
        this.replyId = replyId;
    }
}
