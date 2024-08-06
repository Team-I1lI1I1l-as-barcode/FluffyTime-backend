package com.fluffytime.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    // 새 게시물 등록하기 화면
    @GetMapping("/reg")
    public String postRegView() {
        return "redirect:/html/post/createPost.html"; // 게시물 등록 화면으로 리다이렉트
    }

    // 게시물 상세보기 화면
    @GetMapping("/detail/{id}")
    public String postDetailView(@PathVariable Long id, Model model) {
        model.addAttribute("postId", id); // 모델에 게시물 ID 추가
        return "redirect:/html/post/postDetail.html"; // 게시물 상세보기 화면으로 리다이렉트
    }
}
