package com.fluffytime.domain.board.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private Long userId;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;
    private List<ReplyResponse> replyList;
    private boolean isAuthor; //현재 사용자 = 작성자??
    private String profileImageurl; //프로필 이미지
    private int likeCount;
    private boolean isLiked;

    @Builder
    public CommentResponseDto(Long commentId, Long userId, String content, String nickname,
        LocalDateTime createdAt, List<ReplyResponse> replyList, boolean isAuthor,
        String profileImageurl, int likeCount, boolean isLiked) {
        this.commentId = commentId;
        this.userId = userId;
        this.content = content;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.replyList = replyList;
        this.isAuthor = isAuthor;
        this.profileImageurl = profileImageurl;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}
