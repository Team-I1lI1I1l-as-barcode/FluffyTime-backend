package com.fluffytime.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentLikeResponse {

    private Long userId;
    private String nickname;
    private int likeCount;
    private boolean isLiked;
    private String profileImageurl;
    private String intro;

    @Builder
    public CommentLikeResponse(Long userId, String nickname, int likeCount, boolean isLiked,
        String profileImageurl, String intro) {
        this.userId = userId;
        this.nickname = nickname;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.profileImageurl = profileImageurl;
        this.intro = intro;
    }
}
