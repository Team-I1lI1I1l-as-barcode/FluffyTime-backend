package com.fluffytime.mypage.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResultDto {

    private Boolean result; // 중복 결과 여부
    private String fileUrl; // 이미지 URL을 저장할 필드
}
