package com.fluffytime.domain.notification.controller.api;

import com.fluffytime.domain.notification.dto.request.NotificationRequest;
import com.fluffytime.domain.notification.dto.response.NotificationResponse;
import com.fluffytime.domain.notification.service.NotificationService;
import com.fluffytime.domain.user.dto.response.NotificationUserResponse;
import com.fluffytime.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private final NotificationService notificationService;

    @GetMapping("/stream")
    public SseEmitter streamNotifications(@RequestParam(name = "userId") Long userId) {
        NotificationRequest requestDto = NotificationRequest.builder()
            .userId(userId)
            .build();

        return notificationService.createSseEmitter(requestDto);
    }


    //현재 로그인 한 사용자 정보 조회
    @GetMapping("/current-user")
    public ResponseEntity<NotificationUserResponse> getCurrentUser(
        HttpServletRequest httpServletRequest) {
        User currentUser = notificationService.findByAccessToken(httpServletRequest);

        NotificationUserResponse responseDto = NotificationUserResponse.builder()
            .userId(currentUser.getUserId())
            .nickname(currentUser.getNickname())
            .build();

        return ResponseEntity.ok(responseDto);
    }

    //읽음 상태 바뀜
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
        @PathVariable("notificationId") Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    //알림 전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
        @RequestParam(name = "userId") Long userId) {
        NotificationRequest requestDto = NotificationRequest.builder()
            .userId(userId)
            .build();

        List<NotificationResponse> notificationResponseList = notificationService.getAllNotifications(
            requestDto);
        return ResponseEntity.ok(notificationResponseList);
    }

    //알림 삭제
    @DeleteMapping("/{notificationId}/delete")
    public ResponseEntity<NotificationResponse> deleteNotification(
        @PathVariable(name = "notificationId") Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }
}
