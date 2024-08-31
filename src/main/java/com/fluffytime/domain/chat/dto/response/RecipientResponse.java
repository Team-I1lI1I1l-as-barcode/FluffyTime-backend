package com.fluffytime.domain.chat.dto.response;

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
public class RecipientResponse {

    private String fileUrl; //프로필 사진
    private String nickname; // 닉네임
    private String petName; // 반려동물 이름

}
