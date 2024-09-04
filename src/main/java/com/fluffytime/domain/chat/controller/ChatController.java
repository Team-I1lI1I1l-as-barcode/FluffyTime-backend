package com.fluffytime.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    @GetMapping("/chat")
    public String Chat() {
        return "chat/chat";
    }
}
