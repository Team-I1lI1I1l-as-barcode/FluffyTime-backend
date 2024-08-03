package com.fluffytime.join.dto.reponse;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExistsAccountResponse {

    private
    boolean exists;

    @Builder
    public ExistsAccountResponse(boolean exists) {
        this.exists = exists;
    }
}
