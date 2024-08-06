package com.fluffytime.login.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "/html/login/login.html";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "/html/admin/admin.html";
    }
}
