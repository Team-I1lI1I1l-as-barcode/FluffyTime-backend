package com.fluffytime.domain.user.controller;

import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserPageController {

    private final MyPageService myPageService;

    @GetMapping("/userpages/{nickname}")
    public String userPage(@PathVariable(name = "nickname") String nickname,
        HttpServletRequest httpServletRequest) {
        log.info("유저페이지 접근");
        // 로그인한 유저 찾기
        User user = myPageService.findByAccessToken(httpServletRequest);
        // 로그인한 유저와 유저 페이지의 유저와 동일인인지 체크
        Boolean isAuthorized = myPageService.isUserAuthorized(user.getNickname(), nickname);
        if (isAuthorized) {
            // 본인 일시 마이페이지로 이동
            return "redirect:/mypage/mypage";
        } else {
            // 유저페이지 이동
            return "userpage/userpage";
        }
    }
}
