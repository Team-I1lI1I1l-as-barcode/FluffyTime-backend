package com.fluffytime.join.reponse;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinResponseDto {

    private String email;
    private String nickname;

    @Builder
    public JoinResponseDto(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
