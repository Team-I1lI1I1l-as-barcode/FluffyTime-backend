package com.fluffytime.domain.board.entity;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "reels")
@Entity
@NoArgsConstructor
public class Reels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reels_id", nullable = false)
    private Long reelsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Reels(Post post, String filename, String fileUrl, LocalDateTime createdAt) {
        this.post = post;
        this.filename = filename;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt;
    }
}
