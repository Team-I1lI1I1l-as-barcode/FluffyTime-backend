package com.fluffytime.domain.chat.interceptor;

import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.service.MyPageService;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Component
// WebSocket 핸드 셰이크 과정에서 추가적인 작업을 수행하는 클래스
@RequiredArgsConstructor
public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final MyPageService myPageService;

    @Override
    // 핸드셰이크 전에 실행되는 메서드 정의
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // jwt에서 토큰값을 얻고 사용자 찾기
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        User user = myPageService.findByAccessToken(servletRequest);

        attributes.put("SENDER_USER_NICKNAME", user.getNickname()); // 속성맵에 보내는이 유저 객체 추가하기
        return super.beforeHandshake(request, response, wsHandler,
            attributes); // 부모 클래스의 beforeHandshake 메서드를 호출하고 결과 반환
    }

    @Override
    // 핸드셰이크 후에 실행되는 메서드 정의
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, @Nullable Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex); // 부모 클래스의 afterHandshake 메서드를 호출
    }
}
