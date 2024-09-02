package com.fluffytime.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationUserResponse {

    private Long userId;
    private String nickname;
    private String profileImageurl;

    @Builder
    public NotificationUserResponse(Long userId, String nickname, String profileImageurl) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageurl = profileImageurl;
    }
}
