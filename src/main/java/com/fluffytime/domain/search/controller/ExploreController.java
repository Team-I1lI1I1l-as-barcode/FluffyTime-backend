package com.fluffytime.domain.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ExploreController {

    @GetMapping("/explore")
    public String explorePage() {
        return "explore/explore";
    }
}
