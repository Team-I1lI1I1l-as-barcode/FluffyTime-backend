package com.fluffytime.comment.dto;

import com.fluffytime.domain.Comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.nickname = comment.getUser().getNickname();
        this.createdAt = LocalDateTime.now();
    }
}
