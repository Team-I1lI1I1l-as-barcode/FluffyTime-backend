package com.fluffytime.domain.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReelsResponse {
    private Long reelsId;
    private String filename;
    private String fileUrl;
    private String createdAt;
    private String postContent;
    private String authorNickname;
    private String profileImageUrl;
}

