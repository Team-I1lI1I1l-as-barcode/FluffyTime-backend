package com.fluffytime.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SucceedCertificationResponse {

    private String email;

    @Builder
    public SucceedCertificationResponse(String email) {
        this.email = email;
    }

}
