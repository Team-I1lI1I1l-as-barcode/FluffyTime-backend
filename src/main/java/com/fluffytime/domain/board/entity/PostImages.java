package com.fluffytime.domain.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "post_images")
@Entity
@NoArgsConstructor
public class PostImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long imageId;

    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "filepath", nullable = false, length = 255)
    private String filepath;

    @Column(name = "filesize", nullable = false)
    private Long filesize;

    @Column(name = "mimetype", nullable = false, length = 50)
    private String mimetype;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @PrePersist
    public void Create() {
        this.uploadDate = LocalDateTime.now();
    }

    @Builder
    public PostImages(Long imageId, String filename, String filepath, Long filesize,
        String mimetype,
        String description, LocalDateTime uploadDate, Post post) {
        this.imageId = imageId;
        this.filename = filename;
        this.filepath = filepath;
        this.filesize = filesize;
        this.mimetype = mimetype;
        this.description = description;
        this.uploadDate = uploadDate;
        this.post = post;
    }
}
