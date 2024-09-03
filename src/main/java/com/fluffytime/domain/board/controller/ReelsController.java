package com.fluffytime.domain.board.controller;

import com.fluffytime.domain.board.dto.response.ReelsResponse;
import com.fluffytime.domain.board.service.ReelsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reels")
@RequiredArgsConstructor
public class ReelsController {

    private final ReelsService reelsService;

    @GetMapping
    public String listReels(Model model) {
        List<ReelsResponse> reelsList = reelsService.getAllReels();
        model.addAttribute("reelsList", reelsList);
        return "post/reels";
    }

}