package com.fluffytime.userpage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserPageController {

    @GetMapping("/userpages/{nickname}")
    public String userPage(@PathVariable(name = "nickname") String nickname) {
        log.info("유저페이지 접근");
        return "/html/userpage/userpage.html";
    }
}
