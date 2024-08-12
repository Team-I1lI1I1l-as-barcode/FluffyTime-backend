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
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$", message = "비밀번호는 8자 이상 20자 이하, 숫자, 문자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "닉네임은 숫자, 문자, _, -만 사용가능합니다.")
    @Size(max = 20)
    private String nickname;
}
