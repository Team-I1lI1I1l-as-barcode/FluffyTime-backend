package com.fluffytime.join.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SucceedCertificationResponse {

    private String email;

    @Builder
    public SucceedCertificationResponse(String email) {
        this.email = email;
    }

}
