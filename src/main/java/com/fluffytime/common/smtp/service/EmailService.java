package com.fluffytime.common.smtp.service;

import com.fluffytime.common.exception.global.FailedToSendEmail;
import com.fluffytime.common.smtp.builder.EmailContentBuilder;
import com.fluffytime.user.dto.response.SucceedSendEmailResponse;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

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
