package com.fluffytime.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {


    // Spring Data Redis에서 제공하는 클래스이며, Redis의 pub/sub을 처리하는 리스너 컨테이너 생성
    @Bean
    public RedisMessageListenerContainer redisContainer(
        @Autowired RedisConnectionFactory connectionFactory) {
        // 새로운 RedisMessageListenerContainer 인스턴스 생성
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // Redis 연결 설정
        container.setConnectionFactory(connectionFactory);
        // 설정된 RedisMessageListenerContainer 반환
        return container;
    }


}

