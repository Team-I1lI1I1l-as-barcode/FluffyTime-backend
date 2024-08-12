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
    private boolean isAuthor; //현재 사용자 = 작성자??

    public CommentResponseDto(Comment comment, Long currentUserId) {
        this.commentId = comment.getCommentId();
        this.userId = comment.getUser().getUserId();
        this.content = comment.getContent();
        this.nickname = comment.getUser().getNickname();
        this.createdAt = comment.getCreatedAt();
        this.replyList = comment.getReplyList().stream()
            .map(reply -> new ReplyResponseDto(reply, currentUserId))
            .collect(Collectors.toList());
        this.isAuthor = this.userId.equals(currentUserId);
    }
}
