package com.fluffytime.join.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SucceedSendEmailResponse {

    private String email;

    @Builder
    public SucceedSendEmailResponse(String email) {
        this.email = email;
    }

}
