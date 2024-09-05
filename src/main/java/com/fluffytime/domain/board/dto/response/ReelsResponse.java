package com.fluffytime.domain.board.dto.response;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReelsResponse {
    private Long reelsId;
    private Long postId;
    private Long userId;
    private String filename;
    private String filepath;
    private Long filesize;
    private String mimetype;

    private String content;  // Post의 내용
    private String nickname; // User의 닉네임
    private String profileImageUrl;
    private boolean isBookmarked;
    private int likeCount;
    private boolean isLiked;
}
