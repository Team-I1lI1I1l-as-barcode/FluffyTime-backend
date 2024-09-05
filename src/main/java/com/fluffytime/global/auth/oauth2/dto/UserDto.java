package com.fluffytime.global.auth.oauth2.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private final Long id;
    private final String email;
    private final String nickname;
    private final List<String> roles;
}
