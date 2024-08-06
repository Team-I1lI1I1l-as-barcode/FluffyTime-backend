package com.fluffytime.mypage.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// 프로필을 수정할때 클라이언트가 서버에 보내는 수정된 프로필 정보 DTO
public class ProfileDto {

    private String nickname; // 요청한 닉네임
    private String username; // 요청시 변경된 닉네임
    private String intro; // 소개
    private String petName; // 반려동물 이름
    private String petSex; // 반려동물 성별
    private String petAge; // 반려동물 나이
    private String petCategory; // 반려동물 카테고리
    private String publicStatus; // 프로필 공개/비공개 여부
}
