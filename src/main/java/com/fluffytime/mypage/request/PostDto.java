package com.fluffytime.mypage.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 유저의 게시글 가져오는 DTO
public class PostDto {

    private Long postId; // 게시글 id
    private String fileUrl; // s3i 이미지 저장 url

}
