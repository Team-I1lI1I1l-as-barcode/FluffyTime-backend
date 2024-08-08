package com.fluffytime.join.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckDuplicationResponse {
    private Boolean isExists;

    @Builder
    public CheckDuplicationResponse(Boolean isExists) {
        this.isExists = isExists;
    }
}
