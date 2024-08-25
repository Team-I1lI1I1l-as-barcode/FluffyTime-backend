package com.fluffytime.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FindEmailResponse {
    private final Boolean isExists;
    private final String email;

    @Builder
    public FindEmailResponse(Boolean isExists, String email) {
        this.isExists = isExists;
        this.email = email;
    }
}
