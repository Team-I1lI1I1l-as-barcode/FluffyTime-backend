package com.fluffytime.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
// Redis Pub/Sub 시스템에서 메시지를  redis topic에 발행(publish)하는 역할
// 해당 서비스를 통해 메시지를 발행하면, 대기하고 있던 redis sub 서비스가 메시지를 구독자들에게 전달
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    // convertAndSend() : 지정된 채널에 메시지 발행
    public void publish(String roomId, String message) {
        redisTemplate.convertAndSend(roomId, message); // 메시지를 지정된 채널로 전송
    }
}
