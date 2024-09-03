package com.fluffytime.domain.notification.dto.response;

import com.fluffytime.domain.notification.entity.enums.AdminNotificationType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminNotificationResponse {
    private Long adminNotificationId;
    private String message;
    private AdminNotificationType type;
    private LocalDateTime createdAt;
}
