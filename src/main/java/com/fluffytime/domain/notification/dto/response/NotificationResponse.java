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
    private LocalDateTime createdAt;
    private String profileImageurl;

    @Builder
    public NotificationResponse(Long notificationId, String message, boolean isRead, String type,
        Long userId, Long postId, LocalDateTime createdAt, String profileImageurl) {
        this.notificationId = notificationId;
        this.message = message;
        this.isRead = isRead;
        this.type = type;
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
        this.profileImageurl = profileImageurl;
    }
}
