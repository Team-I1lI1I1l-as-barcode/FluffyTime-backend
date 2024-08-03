package com.fluffytime.join.dto.reponse;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SendEmailResponse {

    private String email;

    @Builder
    public SendEmailResponse(String email) {
        this.email = email;
    }

}
