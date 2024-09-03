package com.fluffytime.domain.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import com.fluffytime.domain.user.entity.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "filepath", nullable = false, length = 255)
    private String filepath;

    @Column(name = "filesize", nullable = false)
    private Long filesize;

    @Column(name = "mimetype", nullable = false, length = 50)
    private String mimetype;

    @Builder
    public Reels(Post post, User user, String filename, String filepath, Long filesize, String mimetype) {
        this.post = post;
        this.user = user;
        this.filename = filename;
        this.filepath = filepath;
        this.filesize = filesize;
        this.mimetype = mimetype;
    }
}