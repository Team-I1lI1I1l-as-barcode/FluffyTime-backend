package com.fluffytime.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MentionResponse {

    private Long mentionId;
    private Long postId;
    private Long commentId;
    private Long replyId;
    private Long mentionedUserId;
    private String mentionedNickname;

    @Builder
    public MentionResponse(Long mentionId, Long postId, Long commentId, Long replyId,
        Long mentionedUserId, String mentionedNickname) {
        this.mentionId = mentionId;
        this.postId = postId;
        this.commentId = commentId;
        this.replyId = replyId;
        this.mentionedUserId = mentionedUserId;
        this.mentionedNickname = mentionedNickname;
    }
}
