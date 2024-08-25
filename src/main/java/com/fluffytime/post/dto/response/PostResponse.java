package com.fluffytime.post.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostResponse { //게시글과 관련된 특정한 응답 데이터를 담기 위해 사용

    private Long postId;
    private String content;
    private List<ImageResponse> imageUrls;
    private String createdAt;
    private String updatedAt;
    //private List<String> tagNames;
    private int likeCount;
    private boolean isLiked;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ImageResponse {

        private Long imageId;
        private String filename;
        private String filepath;
        private Long filesize;
        private String mimetype;
        private String description;
        private String uploadDate;
    }
}
