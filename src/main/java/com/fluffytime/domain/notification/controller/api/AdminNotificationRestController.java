package com.fluffytime.domain.notification.controller.api;

import com.fluffytime.domain.notification.dto.response.AdminNotificationResponse;
import com.fluffytime.domain.notification.service.AdminNotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class AdminNotificationRestController {
    private final AdminNotificationService adminNotificationService;

    @GetMapping("/admin/connect")
    public SseEmitter adminConnect() {
        return adminNotificationService.connect(SecurityContextHolder.getContext().getAuthentication());
    }

    @GetMapping("/admin/joinNotification")
    public ResponseEntity<List<AdminNotificationResponse>> getJoinTypeNotifications() {
        List<AdminNotificationResponse> allAdminNotifications = adminNotificationService.getJoinTypeNotifications();
        return ResponseEntity.status(HttpStatus.OK).body(allAdminNotifications);
    }
    @GetMapping("/admin/postNotification")
    public ResponseEntity<List<AdminNotificationResponse>> getPostTypeNotifications() {
        List<AdminNotificationResponse> allAdminNotifications = adminNotificationService.getPostTypeNotifications();
        return ResponseEntity.status(HttpStatus.OK).body(allAdminNotifications);
    }
}
