package com.fluffytime.mypage.controller;

import com.fluffytime.domain.User;
import com.fluffytime.mypage.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/")
    public String home() {
        return "/html/main.html";
    }

    // 마이페이지
    @GetMapping("/mypage/{nickname}")
    public String myPage(@PathVariable(name = "nickname") String nickname,
        HttpServletRequest httpServletRequest) {
        User user = myPageService.findByAccessToken(httpServletRequest);
        Boolean isAuthorized = myPageService.isUserAuthorized(user.getNickname(), nickname);

        if (isAuthorized) {
            return "/html/mypage/mypage.html";
        } else {
            return "redirect:/mypage/" + user.getNickname();
        }
    }

    // 프로필 편집 페이지
    @GetMapping("/mypage/profile/edit/{nickname}")
    public String profileEdit(@PathVariable(name = "nickname") String nickname,
        HttpServletRequest httpServletRequest) {
        User user = myPageService.findByAccessToken(httpServletRequest);
        Boolean isAuthorized = myPageService.isUserAuthorized(user.getNickname(), nickname);

        if (isAuthorized) {
            return "/html/mypage/profiles/profile.html";
        } else {
            return "redirect:/mypage/profile/edit/" + user.getNickname();
        }
    }
}
