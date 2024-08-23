package com.fluffytime.Tag.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TagResponse {

    private Long tagId;
    private String tagName;

    @Builder
    public TagResponse(Long tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }
}