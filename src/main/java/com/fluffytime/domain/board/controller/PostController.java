package com.fluffytime.domain.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/posts")
public class PostController {

    // 게시물 상세보기 화면
    @GetMapping("/detail/{id}")
    public String postDetailView(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("postId", id); // 모델에 게시물 ID 추가

        return "post/postDetail";
    }
}
