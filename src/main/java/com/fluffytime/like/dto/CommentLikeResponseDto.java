package com.fluffytime.like.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentLikeResponseDto {

    private Long userId;
    private String nickname;
    private int likeCount;
    private boolean isLiked;

    @Builder
    public CommentLikeResponseDto(Long userId, String nickname, int likeCount, boolean isLiked) {
        this.userId = userId;
        this.nickname = nickname;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}
