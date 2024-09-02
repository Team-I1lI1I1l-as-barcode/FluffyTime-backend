package com.fluffytime.domain.board.entity;

import com.fluffytime.domain.board.entity.enums.TempStatus;
import com.fluffytime.domain.notification.entity.Notification;
import com.fluffytime.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "posts")
@Entity
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "temp_status", nullable = false)
    private TempStatus tempStatus;

    @Column(name = "comments_disabled", nullable = false)
    private boolean commentsDisabled = false;

    @Column(name = "hide_like_count", nullable = false)
    private boolean hideLikeCount = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<PostImages> postImages = new ArrayList<>();

    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<TagPost> tagPosts = new ArrayList<>();

    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Bookmark> bookmarkList = new ArrayList<>();

    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Mention> mentions = new ArrayList<>();

    @Column(name = "comments_disabled", nullable = false)
    private boolean commentsDisabled = false;

    @Builder
    public Post(Long postId, String content, LocalDateTime createdAt,
        LocalDateTime updatedAt, TempStatus tempStatus, User user,
        boolean hideLikeCount, boolean commentsDisabled) {
        this.postId = postId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tempStatus = tempStatus;
        this.user = user;
        this.hideLikeCount = hideLikeCount;
        this.commentsDisabled = commentsDisabled;
    }
}