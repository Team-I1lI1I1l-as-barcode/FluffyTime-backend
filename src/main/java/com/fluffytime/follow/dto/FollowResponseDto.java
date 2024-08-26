package com.fluffytime.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponseDto {

    private Long followMappingId;
    private Long followingUserId;
    private Long followedUserId;
}
