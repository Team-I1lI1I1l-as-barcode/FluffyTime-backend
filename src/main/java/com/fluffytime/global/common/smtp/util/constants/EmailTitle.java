package com.fluffytime.global.common.smtp.util.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTitle {
    PASSWORD_CHANGE_EMAIL_TITLE("[ FluffyTime - 반려동물 전용 SNS ] 비밀번호 변경 메일입니다."),
    CERTIFICATION_EMAIL_TITLE("[ FluffyTime - 반려동물 전용 SNS ] 가입 인증 메일입니다.");

    private final String title;
}
