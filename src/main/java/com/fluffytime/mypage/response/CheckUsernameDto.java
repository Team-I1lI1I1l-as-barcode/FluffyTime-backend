package com.fluffytime.mypage.response;

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

// 닉네임 중복 여부 결과를 담아서 클라이언트에게 보내는 DTO
public class CheckUsernameDto {

    private String code; // 상태코드
    private String message; // 상태 메시지
    private Boolean result; // 중복 결과 여부
}
