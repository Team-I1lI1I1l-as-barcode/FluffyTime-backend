package com.fluffytime.join.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JoinResponse {

    private String email;
    private String nickname;

    @Builder
    public JoinResponse(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
