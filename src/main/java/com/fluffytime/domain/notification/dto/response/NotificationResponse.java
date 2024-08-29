package com.fluffytime.domain.notification.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationResponse {

    private Long notificationId;
    private String message;
    private boolean isRead;
    private String type;
    private Long userId;
    private Long postId;
    private Long commentId;
    private Long replyId;
    private LocalDateTime createdAt;
    private String profileImageurl;

    @Builder
    public NotificationResponse(Long notificationId, String message, boolean isRead, String type,
        Long userId, Long postId, LocalDateTime createdAt, String profileImageurl, Long commentId,
        Long replyId) {
        this.notificationId = notificationId;
        this.message = message;
        this.isRead = isRead;
        this.type = type;
        this.userId = userId;
        this.commentId = commentId;
        this.postId = postId;
        this.replyId = replyId;
        this.createdAt = createdAt;
        this.profileImageurl = profileImageurl;
    }
}
