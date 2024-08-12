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
        return "/html/join/join.html";
    }

    @GetMapping("/email-certificate/{email}")
    public String emailCertificatePage() {
        return "/html/join/EmailCertificationStay.html";
    }

    @GetMapping("/email-certificate/result/{email}")
    public String emailCertificateResultPage() {
        return "/html/join/EmailCertificationResult.html";
    }

    @GetMapping("/success")
    public String joinSuccessPage() {
        return "/html/join/joinSuccess.html";
    }

    @GetMapping("/fail")
    public String joinFailPage() {
        return "/html/join/joinFail.html";
    }
}
