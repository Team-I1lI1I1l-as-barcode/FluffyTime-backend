package com.fluffytime.join.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/join")
public class joinController {

    @GetMapping
    public String joinPage() {
        return "join/join";
    }

    @GetMapping("/email-certificate/{email}")
    public String emailCertificatePage() {
        return "join/EmailCertificationStay";
    }

    @GetMapping("/email-certificate/result/{email}")
    public String emailCertificateResultPage() {
        return "join/EmailCertificationResult";
    }

    @GetMapping("/success")
    public String joinSuccessPage() {
        return "join/joinSuccess";
    }

    @GetMapping("/fail")
    public String joinFailPage() {
        return "join/joinFail";
    }
}
