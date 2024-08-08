package com.fluffytime.post.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostResponse {

    private Long postId;
    private String content;
    private List<String> imageUrls;
    private String createdAt;
    private String updatedAt;
}