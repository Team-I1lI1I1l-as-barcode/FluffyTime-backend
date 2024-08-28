package com.fluffytime.domain.notification.service;

import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.notification.dto.request.NotificationRequest;
import com.fluffytime.domain.notification.dto.response.NotificationResponse;
import com.fluffytime.domain.notification.entity.Notification;
import com.fluffytime.domain.notification.exception.NotificationNotFound;
import com.fluffytime.domain.notification.repository.NotificationRepository;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.global.common.exception.global.UserNotFound;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SseEmitters sseEmitters;
    private final JwtTokenizer jwtTokenizer;

    @Transactional(readOnly = true)
    public SseEmitter createSseEmitter(NotificationRequest requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(UserNotFound::new);

        return sseEmitters.createForUser(user.getUserId());
    }

    //댓글 알림 생성
    @Transactional
    public void createCommentsNotification(Post post, User commentAuthor) {
        String message = commentAuthor.getNickname() + "님이 회원님의 게시글에 댓글을 달았습니다";
        User user = userRepository.findById(post.getUser().getUserId())
            .orElseThrow(UserNotFound::new);

        String profileImageurl = getProfileImageUrl(commentAuthor);

        Notification notification = Notification.builder()
            .message(message)
            .isRead(false)
            .user(user)
            .post(post)
            .type("comment")
            .profileImageurl(profileImageurl)
            .build();
        notificationRepository.save(notification);

        NotificationResponse responseDto = convertToDto(notification);
        sseEmitters.sendToUser(user.getUserId(), responseDto);
    }

    //알림 읽음 표시 상태 바꿈
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(NotificationNotFound::new);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    //모든 알림 조회
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications(NotificationRequest requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(UserNotFound::new);
        List<Notification> notifications = notificationRepository.findByUserOrderByIsReadAscCreatedAtDesc(
            user);

        return notifications.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    //알림 삭제
    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(NotificationNotFound::new);

        notificationRepository.delete(notification);
    }

    //accessToken으로 사용자 찾기
    @Transactional(readOnly = true)
    public User findByAccessToken(HttpServletRequest httpServletRequest) {
        String accessToken = jwtTokenizer.getTokenFromCookie(httpServletRequest, "accessToken");

        Long userId = null;
        userId = jwtTokenizer.getUserIdFromToken(accessToken);
        User user = findUserById(userId).orElseThrow(UserNotFound::new);
        return user;
    }

    //사용자 조회
    public Optional<User> findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user;
    }

    //Notification response convert
    public NotificationResponse convertToDto(Notification notification) {
        return NotificationResponse.builder()
            .notificationId(notification.getNotificationId())
            .message(notification.getMessage())
            .isRead(notification.isRead())
            .type(notification.getType())
            .userId(notification.getUser().getUserId())
            .postId(notification.getPost().getPostId())
            .createdAt(notification.getCreatedAt())
            .profileImageurl(notification.getProfileImageurl())
            .build();
    }

    //프로필 이미지 response
    private String getProfileImageUrl(User user) {
        return Optional.ofNullable(user.getProfile())
            .flatMap(profile -> Optional.ofNullable(profile.getProfileImages()))
            .map(profileImages -> profileImages.getFilePath())
            .orElse("/image/profile/profile.png");
    }
}
