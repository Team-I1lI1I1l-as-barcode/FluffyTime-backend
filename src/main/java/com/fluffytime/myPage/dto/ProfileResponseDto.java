package com.fluffytime.myPage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDto {

    private String code; // 상태코드
    private String message; // 상태 메시지
    private String nickname; // 닉네임
    private String email; // 이메일
    private String intro; // 소개
    private String petName; // 반려동물 이름
    private String petSex; // 반려동물 성별
    private Long petAge; // 반려동물 나이
    private String petCategory; // 반려동물 카테고리
    private String publicStatus; // 프로필 공개/비공개 여부
}
