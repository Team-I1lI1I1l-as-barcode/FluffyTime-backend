package com.fluffytime.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {

    // 테스트용 admin 화면
    @GetMapping("/admin")
    public String adminPage() {
        return "admin/admin";
    }
}
