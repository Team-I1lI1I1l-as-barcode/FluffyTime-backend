package com.fluffytime.join.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class joinController {


    @GetMapping("/join")
    public String joinPage() {
        return "/html/join/joinForm.html";
    }
}
