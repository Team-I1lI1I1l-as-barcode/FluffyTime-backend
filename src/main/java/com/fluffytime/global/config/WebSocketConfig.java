package com.fluffytime.global.config;

import com.fluffytime.domain.chat.interceptor.CustomHandshakeInterceptor;
import com.fluffytime.domain.chat.service.MyWebSocketHandler;
import com.fluffytime.domain.chat.service.RedisMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // WebSocket 기능 활성화
@RequiredArgsConstructor
// 웹소켓을 설정하고, 특정 경로에 대해 myWebSocketHandler를 핸들러로 등록하는 역할
public class WebSocketConfig implements WebSocketConfigurer {

    // WebSocket 핸드 셰이크 과정에서 추가적인 작업을 수행하는 클래스
    private final CustomHandshakeInterceptor customHandshakeInterceptor;
    private final RedisMessagePublisher redisMessagePublisher;

    @Override
    // 특정 경로(/ws)에 대해 WebSocket 핸들러(myWebSocketHandler) 등록
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandler(), "/ws")
            .setAllowedOrigins("*") // 모든 출처 허용
            .addInterceptors(customHandshakeInterceptor); // 핸드셰이크 인터셉터 추가
    }

    @Bean
    public MyWebSocketHandler myWebSocketHandler() {
        return new MyWebSocketHandler(redisMessagePublisher);
    }
}