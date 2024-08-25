package com.fluffytime.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class LoginUserRequest {

    @NotEmpty
    @Email
    private final String email;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$")
    private final String password;

    @Builder
    public LoginUserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
