package com.fluffytime.domain.chat.controller.api;

import com.fluffytime.domain.chat.dto.response.ChatResponse;
import com.fluffytime.domain.chat.dto.response.ChatRoomListResponse;
import com.fluffytime.domain.chat.repository.ChatRoomRepository;
import com.fluffytime.domain.chat.service.ChatServcie;
import com.fluffytime.domain.chat.service.RedisMessagePublisher;
import com.fluffytime.domain.chat.service.RedisMessageSubscriber;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/chat")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatApiController {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisMessagePublisher redisMessagePublisher;
    private final RedisMessageSubscriber redisMessageSubscriber;
    private final ChatServcie chatServcie;
    private final ChatRoomRepository chatRoomRepository;

    // 토픽 목록
    @GetMapping("/topics")
    public ResponseEntity<ChatRoomListResponse> getTopicAll(HttpServletRequest request) {
        ChatRoomListResponse chatRoomListResponse = chatServcie.getTopicList(request);
        return ResponseEntity.status(HttpStatus.OK).body(chatRoomListResponse);
    }

    // 토픽 생성
    @PutMapping("/topics/{nickname}")
    public ResponseEntity<ChatResponse> createTopic(
        @PathVariable(name = "nickname") String nickname, HttpServletRequest request) {
        ChatResponse chatResponse = chatServcie.createTopic(nickname, request);
        return ResponseEntity.status(HttpStatus.OK).body(chatResponse);
    }

    // 토픽 참여
    @GetMapping("/topics/{roomName}")
    public ResponseEntity<ChatResponse> JoinTopic(
        @PathVariable(name = "roomName") String roomName) {

        ChatResponse chatResponse = chatServcie.JoinTopic(roomName);
        return ResponseEntity.status(HttpStatus.OK).body(chatResponse);
    }
//    // 토픽 제거
//    @DeleteMapping("/topics/{roomName}")
//    public void deleteTopic( @PathVariable(name = "roomName") String roomName) {
//        ChannelTopic channel = channels.get(name);
//        redisMessageListener.removeMessageListener(redisSubscriber, channel);
//        channels.remove(name);
//    }

}
