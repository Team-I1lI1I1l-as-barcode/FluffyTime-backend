package com.fluffytime.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor //getter + setter + 기본 생성자(NoArgsConstructor) = JSON 역직렬화
@AllArgsConstructor
public class CommentRequestDto {

    private Long postId;
    private Long userId;
    private String content;
}
