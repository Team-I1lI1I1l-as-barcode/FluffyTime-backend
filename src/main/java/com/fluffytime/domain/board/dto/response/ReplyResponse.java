package com.fluffytime.domain.board.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReplyResponse {

    private Long replyId;
    private Long userId;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;
    private boolean isAuthor; //현재 사용자 = 작성자??
    private String profileImageurl;
    private int likeCount;
    private boolean isLiked;

    @Builder
    public ReplyResponse(Long replyId, Long userId, String content, String nickname,
        LocalDateTime createdAt, boolean isAuthor, String profileImageurl, int likeCount,
        boolean isLiked) {
        this.replyId = replyId;
        this.userId = userId;
        this.content = content;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.isAuthor = isAuthor;
        this.profileImageurl = profileImageurl;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}
