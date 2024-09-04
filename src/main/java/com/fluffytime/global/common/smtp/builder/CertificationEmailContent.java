package com.fluffytime.global.common.smtp.builder;

// 가입 인증 링크 메일 Content
public class CertificationEmailContent implements EmailContentBuilder {

    @Override
    public String getContent(String email) {
        String certificationLink =
            "http://fluffytime.kro.kr/join/email-certificate/result/" + email;
        String certificationMessage = "";
        certificationMessage += "<h1 style='text-align: center;'>[ FluffyTime - 반려동물 전용 SNS ] 가입 인증 메일</h1>";
        certificationMessage += "<h3 style='text-align: center;'>이메일 인증을 완료하세요:</h3>";
        certificationMessage +=
            "<div style='text-align: center;'>아래 링크를 클릭하여 이메일 인증을 완료하세요</div>" +
                "<div style='text-align: center;'>링크 클릭 후 인증 화면의 완료 버튼을 반드시 클릭하여주세요.</div>" +
                "<div style='text-align: center; margin-top: 30px;'><a href='" + certificationLink +
                "' style='font-size: 32px; letter-spacing: 8px;'>인증완료</a></div>";
        return certificationMessage;
    }
}
