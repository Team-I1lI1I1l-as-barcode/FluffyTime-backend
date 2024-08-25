package com.fluffytime.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class FindEmailRequest {
    @NotEmpty
    @Email
    private String email;

    @Builder
    public FindEmailRequest(String email) {
        this.email = email;
    }
}
