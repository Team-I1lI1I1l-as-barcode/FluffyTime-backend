package com.fluffytime.myPage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto {

    private String nickname;
    private String email;
    private String intro;
    private String petName;
    private String petSex;
    private Long petAge;
    private String petCategory;
}
