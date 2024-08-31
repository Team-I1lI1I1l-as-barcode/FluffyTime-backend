package com.fluffytime.domain.chat.controller;

import com.fluffytime.domain.chat.repository.ChatRoomRepository;
import com.fluffytime.domain.chat.service.ChatServcie;
import com.fluffytime.domain.chat.service.RedisMessagePublisher;
import com.fluffytime.domain.chat.service.RedisMessageSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisMessagePublisher redisMessagePublisher;
    private final RedisMessageSubscriber redisMessageSubscriber;
    private final ChatServcie chatServcie;
    private final ChatRoomRepository chatRoomRepository;


    @GetMapping("/chat")
    public String Chat() {
        return "chat/chat";
    }
}
