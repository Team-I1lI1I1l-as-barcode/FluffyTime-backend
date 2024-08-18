package com.fluffytime.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class UserController {
    // 로그인 화면
    @GetMapping("/login")
    public String loginPage() {
        return "login/login";
    }

    // 회원가입화면
    @GetMapping("/join")
    public String joinPage() {
        return "join/join";
    }

    // 회원가입 이메일 인증 대기 화면
    @GetMapping("/join/email-certificate/{email}")
    public String emailCertificatePage() {
        return "join/EmailCertificationStay";
    }

    // 회원가입 이메일 인증 결과 화면
    @GetMapping("/join/email-certificate/result/{email}")
    public String emailCertificateResultPage() {
        return "join/EmailCertificationResult";
    }

    // 회원가입 성공 화면
    @GetMapping("/join/success")
    public String joinSuccessPage() {
        return "join/joinSuccess";
    }

    // 회원가입 실패 화면
    @GetMapping("/join/fail")
    public String joinFailPage() {
        return "join/joinFail";
    }

    // 테스트용 admin 화면
    @GetMapping("/admin")
    public String adminPage() {
        return "admin/admin";
    }
}
