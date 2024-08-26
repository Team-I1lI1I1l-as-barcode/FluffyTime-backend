package com.fluffytime.Tag.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequest {

    private String tagName;
    private Long postId; // Post ID를 받아오기 위해 필드 추가

    @Builder
    public TagRequest(String tagName, Long postId) {
        this.tagName = tagName;
        this.postId = postId;
    }
}