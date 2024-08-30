package com.fluffytime.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowListResponse {

    private Long myUserId;
    private Long targetUserId;
    private String nickname;
    private String profileImageUrl;
    private String intro;

}
