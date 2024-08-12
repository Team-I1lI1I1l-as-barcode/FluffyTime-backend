package com.fluffytime.join.service;

import static com.fluffytime.join.dto.response.JoinResponseCode.SUCCEED_EMAIL_CERTIFICATION;
import static com.fluffytime.join.dto.response.JoinResponseCode.SUCCESS_SEND_CERTIFICATION_EMAIL;

import com.fluffytime.join.dao.EmailCertificationDao;
import com.fluffytime.join.dto.TempUser;
import com.fluffytime.join.dto.response.ApiResponse;
import com.fluffytime.join.dto.response.SendEmailResponse;
import com.fluffytime.join.dto.response.SucceedCertificationResponse;
import com.fluffytime.join.exception.FailedToSendCertificationEmail;
import com.fluffytime.join.exception.NotFoundTempUser;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificationService {

    private final JavaMailSender javaMailSender;
    private final EmailCertificationDao emailCertificationDao;

    private final String SUBJECT = "[ FluffyTime - 반려동물 전용 SNS ] 가입 인증 메일입니다.";


    // 인증 성공 or 실패 응답을 구현해야함
    public ApiResponse<SucceedCertificationResponse> certificateEmail(String email) {
        TempUser user = emailCertificationDao.getTempUser(email)
            .orElseThrow(NotFoundTempUser::new);
        user.successCertification();
        emailCertificationDao.saveEmailCertificationTempUser(user);

        SucceedCertificationResponse response = SucceedCertificationResponse.builder()
            .email(email)
            .build();

        return ApiResponse.response(SUCCEED_EMAIL_CERTIFICATION, response);
    }

    // 메일 제작 및 전송
    public ApiResponse<SendEmailResponse> sendCertificationMail(String email) {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getCertificationMessage(email);

            messageHelper.setTo(email);
            messageHelper.setSubject(SUBJECT);
            messageHelper.setText(htmlContent, true);
            javaMailSender.send(message);

            SendEmailResponse sendEmailResponse = SendEmailResponse.builder()
                .email(email)
                .build();

            return ApiResponse.response(SUCCESS_SEND_CERTIFICATION_EMAIL, sendEmailResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FailedToSendCertificationEmail();
        }
    }

    private String getCertificationMessage(String email) {
        String certificationLink =
            "http://localhost:8080/join/email-certificate/result/" + email;
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
