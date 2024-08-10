package com.fluffytime.join.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class JoinRequest {

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$", message = "패스워드는 영문 대.소문자, 숫자, 특수문자를 포함한 8~20자이어야 합니다.")
    private String password;

    @NotEmpty
    @Size(max = 20)
    private String nickname;
}
