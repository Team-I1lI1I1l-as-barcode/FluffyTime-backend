package com.fluffytime.Tag.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequest {

    private String tagName;

    @Builder
    public TagRequest(String tagName) {
        this.tagName = tagName;
    }
}