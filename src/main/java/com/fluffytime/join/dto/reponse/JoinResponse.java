package com.fluffytime.join.dto.reponse;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinResponse {

    private String email;
    private String nickname;

    @Builder
    public JoinResponse(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
