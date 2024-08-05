package com.fluffytime.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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

//    @Column(name = "title", nullable = false)
//    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "temp_status", nullable = false)
    private TempStatus tempStatus;

    @ElementCollection
    private List<String> imageUrls;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Post(Long postId, String title, String content, LocalDateTime createdAt,
        LocalDateTime updatedAt, TempStatus tempStatus, List<String> imageUrls, User user) {
        this.postId = postId;
        //this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tempStatus = tempStatus;
        this.imageUrls = imageUrls;
        this.user = user;
    }
}