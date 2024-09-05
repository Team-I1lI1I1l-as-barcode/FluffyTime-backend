package com.fluffytime.domain.user.dto.response;

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
@Builder
// 마이페이지 정보를 클라이언트에게 보내는 응답 DTO
public class MyPageInformationResponse {

    private String nickname;  // 사용자 이름
    private List<PostResponse> postsList;// 게시물 리스트
    private List<PostResponse> bookmarkList; // 북마크 리스트
    private List<PostResponse> tagePostList; //태그된 게시물 리스트
    private String petName; // 애완동물 이름
    private String petSex;   // 애완동물 성별
    private Long petAge;  // 애완동물 나이
    private String intro;    // 소개글
    private String fileUrl; // 프로필 사진 등록 URL

}