package com.fluffytime.global.common.smtp.service;

import com.fluffytime.global.common.exception.global.FailedToSendEmail;
import com.fluffytime.global.common.smtp.builder.EmailContentBuilder;
import com.fluffytime.domain.user.dto.response.SucceedSendEmailResponse;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    // html 메일 전송 메서드
    public SucceedSendEmailResponse sendHtmlMail(String email, String subject,
        EmailContentBuilder emailBuilder) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(emailBuilder.getContent(email), true);
            javaMailSender.send(message);

            return SucceedSendEmailResponse.builder()
                .email(email)
                .build();

        } catch (Exception e) {
            throw new FailedToSendEmail();
        }
    }
}
