package com.fluffytime.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentRequestDto {

    private Long postId;
    private Long userId;
    private String content;
}
