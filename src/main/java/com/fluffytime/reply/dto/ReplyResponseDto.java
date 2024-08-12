package com.fluffytime.reply.dto;

import com.fluffytime.domain.Reply;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponseDto {

    private Long replyId;
    private Long userId;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;
    private boolean isAuthor; //현재 사용자 = 작성자??

    public ReplyResponseDto(Reply reply, Long currentUserId) {
        this.replyId = reply.getReplyId();
        this.userId = reply.getUser().getUserId();
        this.content = reply.getContent();
        this.nickname = reply.getUser().getNickname();
        this.createdAt = reply.getCreatedAt();
        this.isAuthor = this.userId.equals(currentUserId);
    }
}
