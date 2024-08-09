package com.fluffytime.comment.dto;

import com.fluffytime.domain.Comment;
import com.fluffytime.reply.dto.ReplyResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
    private Long userId;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;
    private List<ReplyResponseDto> replyList;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.userId = comment.getUser().getUserId();
        this.content = comment.getContent();
        this.nickname = comment.getUser().getNickname();
        this.createdAt = LocalDateTime.now();
        this.replyList = comment.getReplyList().stream()
            .map(ReplyResponseDto::new)
            .collect(Collectors.toList());
    }
}
