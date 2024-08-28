package com.fluffytime.domain.notification.entity;

import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Table(name = "notifications")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @Column(name = "notification_msg")
    private String message;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "notification_type")
    private String type;

    @Column(name = "profile_img")
    private String profileImageurl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notification(String message, boolean isRead, User user, Post post, String type,
        String profileImageurl) {
        this.message = message;
        this.isRead = isRead;
        this.user = user;
        this.post = post;
        this.type = type;
        this.profileImageurl = profileImageurl;
    }
}
