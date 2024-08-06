package com.fluffytime.mypage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPageController {

    @GetMapping("/")
    public String home() {
        return "/html/index.html";
    }

    // 마이페이지
    @GetMapping("/mypage/{nickname}")
    public String mypage() {
        return "/html/mypage/mypage.html";
    }

    // 프로필 편집 페이지
    @GetMapping("/mypage/profile/edit/{nickname}")
    public String profileEdit() {
        return "/html/mypage/profiles/profile.html";
    }
}
