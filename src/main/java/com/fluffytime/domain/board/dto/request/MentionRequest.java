package com.fluffytime.domain.board.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MentionRequest {

    private Long postId;
    private Long commentId;
    private Long replyId;
    private Long mentionedUserId;
    private String content;
}
