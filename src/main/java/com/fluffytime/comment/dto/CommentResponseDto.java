package com.fluffytime.comment.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;
}
