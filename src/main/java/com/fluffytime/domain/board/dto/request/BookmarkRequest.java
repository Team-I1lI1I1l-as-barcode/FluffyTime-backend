package com.fluffytime.domain.board.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkRequest {

    private Long userId;
    private Long postId;
}