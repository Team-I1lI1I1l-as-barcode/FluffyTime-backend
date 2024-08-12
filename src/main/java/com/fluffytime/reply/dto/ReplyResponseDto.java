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
    private String content;
    private String nickname;
    private LocalDateTime createdAt;

    public ReplyResponseDto(Reply reply) {
        this.replyId = reply.getReplyId();
        this.content = reply.getContent();
        this.nickname = reply.getUser().getNickname();
        this.createdAt = LocalDateTime.now();
    }
}
