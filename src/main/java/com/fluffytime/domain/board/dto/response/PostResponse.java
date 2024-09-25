package com.fluffytime.domain.board.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostResponse {

    //현재 로그인한 유저 아이디
    private Long myUserId;

    //게시글과 관련된 특정한 응답 데이터를 담기 위해 사용
    private Long postId;
    private String content;
    private List<ImageResponse> imageUrls;
    //    private List<TagsResponse> tags;
    private List<String> tags;
    private String createdAt;
    private String updatedAt;
    private int likeCount;
    private boolean isLiked;
    private boolean commentsDisabled;
    private boolean hideLikeCount;

    //작성자 정보
    private Long targetUserId;
    private String nickname;
    private String profileImageurl;
    private String petName;
    private String petSex;
    private Long petAge;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ImageResponse {

        private Long imageId;
        private String filename;
        private String filepath;
        private Long filesize;
        private String mimetype;
        private String uploadDate;
    }

/*    @Getter
    @Setter
    @AllArgsConstructor
    public static class TagsResponse {
        private Long tagId;
        private String tagName;
    }*/
}
