package com.fluffytime.comment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommentController {

    @GetMapping("/test")
    public String test() {
        return "html/comment/testView.html";
    }
}
