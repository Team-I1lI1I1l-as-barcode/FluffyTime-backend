package com.fluffytime.join.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class joinController {

    @GetMapping("/join")
    public String joinPage() {
        return "/html/join/joinForm.html";
    }

    @GetMapping("/join/email-certificate/{email}")
    public String emailCertificatePage() {
        return "/html/join/EmailCertificationForm.html";
    }

    @GetMapping("/join/email-certificate/result/{email}")
    public String emailCertificateResultPage() {
        return "/html/join/EmailCertificationResultForm.html";
    }

    @GetMapping("/join/success")
    public String joinSuccessPage() {
        return "/html/join/success.html";
    }

    @GetMapping("/join/fail")
    public String joinFailPage() {
        return "/html/join/fail.html";
    }
}
