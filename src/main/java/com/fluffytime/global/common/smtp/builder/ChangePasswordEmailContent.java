package com.fluffytime.global.common.smtp.builder;

// 비밀번호 변경 링크 메일 content
public class ChangePasswordEmailContent implements EmailContentBuilder {

    @Override
    public String getContent(String email) {
        String certificationLink =
            "https://fluffytime.kro.kr/login/change-password?email=" + email;
        String certificationMessage = "";
        certificationMessage += "<h1 style='text-align: center;'>[ FluffyTime - 반려동물 전용 SNS ] 비밀번호 변경 메일</h1>";
        certificationMessage += "<h3 style='text-align: center;'>비밀번호를 변경하세요.</h3>";
        certificationMessage +=
            "<div style='text-align: center;'>아래 링크를 클릭하여 비밀번호를 변경하세요.</div>" +
                "<div style='text-align: center; margin-top: 30px;'><a href='" + certificationLink +
                "' style='font-size: 32px; letter-spacing: 8px;'>비밀번호 변경하러 가기</a></div>";
        return certificationMessage;
    }
}
