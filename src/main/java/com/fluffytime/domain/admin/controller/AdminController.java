package com.fluffytime.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @GetMapping
    public String adminPage() {
        return "admin/admin";
    }

    @GetMapping("/user")
    public String userManagementPage() {
        return "admin/user/userManagementPage";
    }
}
