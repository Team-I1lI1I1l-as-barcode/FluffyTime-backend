package com.fluffytime.domain.notification.service;

import static com.fluffytime.domain.notification.entity.enums.AdminNotificationType.*;
import static com.fluffytime.domain.notification.util.constants.AdminNotificationMessage.DELETE_POST_NOTICE;
import static com.fluffytime.domain.notification.util.constants.AdminNotificationMessage.JOIN_NOTICE;
import static com.fluffytime.domain.notification.util.constants.AdminNotificationMessage.REG_POST_NOTICE;
import static com.fluffytime.domain.notification.util.constants.AdminNotificationMessage.WITHDRAW_NOTICE;

import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.notification.dto.response.AdminNotificationResponse;
import com.fluffytime.domain.notification.entity.AdminNotification;
import com.fluffytime.domain.notification.repository.AdminNotificationRepository;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.global.auth.jwt.dto.CustomUserDetails;
import com.fluffytime.global.common.exception.global.BadRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminNotificationService {

    private final AdminNotificationRepository adminNotificationRepository;
    private final SseEmitters sseEmitters;

    @Transactional
    public SseEmitter connect(Authentication authentication) {
        Long userId = getUserId(authentication);

        return sseEmitters.createForAdmin(userId);
    }

    @Transactional
    public void createJoinNotification(User user) {

        String message = JOIN_NOTICE.joinNotice(user.getEmail());

        AdminNotification adminNotification = AdminNotification.builder()
            .message(message)
            .type(JOIN_NOTIFICATION)
            .build();

        adminNotificationRepository.save(adminNotification);

        sseEmitters.sendToAllAdmin(convertToResponse(adminNotification));
    }

    @Transactional
    public void withdrawJoinNotification(User user) {
        String message = WITHDRAW_NOTICE.withdrawNotice(user.getEmail());

        AdminNotification adminNotification = AdminNotification.builder()
            .message(message)
            .type(JOIN_NOTIFICATION)
            .build();

        adminNotificationRepository.save(adminNotification);

        sseEmitters.sendToAllAdmin(convertToResponse(adminNotification));
    }

    @Transactional
    public List<AdminNotificationResponse> getJoinTypeNotifications() {
        return adminNotificationRepository.findByTypeOrderByCreatedAtAsc(JOIN_NOTIFICATION)
            .stream().map(this::convertToResponse).toList();
    }

    @Transactional
    public List<AdminNotificationResponse> getPostTypeNotifications() {
        return adminNotificationRepository.findByTypeOrderByCreatedAtAsc(POST_NOTIFICATION)
            .stream().map(this::convertToResponse).toList();
    }

    @Transactional
    public void createRegPostNotification(User user,Post post) {
        String message = REG_POST_NOTICE.regPostNotice(user.getEmail(), post.getPostId());

        AdminNotification adminNotification = AdminNotification.builder()
            .message(message)
            .type(POST_NOTIFICATION)
            .build();
        adminNotificationRepository.save(adminNotification);

        sseEmitters.sendToAllAdmin(convertToResponse(adminNotification));
    }

    @Transactional
    public void createDeletePostNotification(User user, Post post) {
        String message = DELETE_POST_NOTICE.deletePostNotice(user.getEmail(), post.getPostId());

        AdminNotification adminNotification = AdminNotification.builder()
            .message(message)
            .type(POST_NOTIFICATION)
            .build();

        adminNotificationRepository.save(adminNotification);

        sseEmitters.sendToAllAdmin(convertToResponse(adminNotification));
    }

    private Long getUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if(principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getId();
        } else {
            throw new BadRequest();
        }
    }

    private AdminNotificationResponse convertToResponse(AdminNotification adminNotification) {
        return AdminNotificationResponse.builder()
            .adminNotificationId(adminNotification.getNotificationId())
            .message(adminNotification.getMessage())
            .type(adminNotification.getType())
            .createdAt(adminNotification.getCreatedAt())
            .build();
    }
}
