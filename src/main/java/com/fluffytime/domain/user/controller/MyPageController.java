package com.fluffytime.domain.user.controller;

import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping()
    public String home() {
        return "main";
    }

    // 마이페이지
    @GetMapping("/mypage/{nickname}")
    public String myPage(@PathVariable(name = "nickname") String nickname,
        HttpServletRequest httpServletRequest) {
        // 로그인한 유저 찾기
        User user = myPageService.findByAccessToken(httpServletRequest);
        // 로그인한 유저와 마이페이지의 유저와 동일인인지 체크
        Boolean isAuthorized = myPageService.isUserAuthorized(user.getNickname(), nickname);

        if (isAuthorized) {
            // 동일인일시 마이페이지로 이동
            return "mypage/mypage";
        } else {
            // 다른 마이페이지 접근시 본인 마이페이지로 이동
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
            return "mypage/profiles/profile";
        } else {
            return "redirect:/mypage/profile/edit/" + user.getNickname();
        }
    }
}
