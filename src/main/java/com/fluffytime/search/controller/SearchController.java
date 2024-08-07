package com.fluffytime.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SearchController {

    @GetMapping("/search")
    public String searchPage() {
        return "/html/search/search.html";
    }

}
