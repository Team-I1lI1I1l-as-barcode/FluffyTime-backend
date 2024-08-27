package com.fluffytime.domain.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommentController {

    @GetMapping("/test")
    public String test() {
        return "comment/testView";
    }
}
