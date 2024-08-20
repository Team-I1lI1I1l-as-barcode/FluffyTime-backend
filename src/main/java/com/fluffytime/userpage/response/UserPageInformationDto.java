package com.fluffytime.userpage.response;

import com.fluffytime.mypage.response.PostDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
// 유저페이지 정보를 클라이언트에게 보내는 응답 DTO
public class UserPageInformationDto {

    private String nickname;  // 사용자 이름
    private List<PostDto> postsList;// 게시물 리스트
    private int followerCount;  // 팔로워 수
    private int followCount;// 팔로우 수
    private String petName; // 애완동물 이름
    private String petSex;   // 애완동물 성별
    private Long petAge;  // 애완동물 나이
    private String intro;    // 소개글
    private String fileUrl; // 프로필 사진 등록 URL

    @Builder
    public UserPageInformationDto(String nickname,
        List<PostDto> postsList,
        String petName, String petSex, Long petAge, String intro, String fileUrl) {
        this.nickname = nickname;
        this.postsList = postsList;
        this.petName = petName;
        this.petSex = petSex;
        this.petAge = petAge;
        this.intro = intro;
        this.fileUrl = fileUrl;
    }
}