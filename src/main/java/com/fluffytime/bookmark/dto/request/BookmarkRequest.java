package com.fluffytime.bookmark.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkRequest {

    private Long userId;
    private Long postId;
}