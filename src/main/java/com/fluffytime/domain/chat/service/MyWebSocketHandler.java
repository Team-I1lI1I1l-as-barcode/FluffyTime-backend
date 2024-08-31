package com.fluffytime.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

// TextWebSocketHandler를 확장하여 WebSocket 메시지를 처리하는 핸들러
@RequiredArgsConstructor
@Slf4j
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final RedisMessagePublisher redisMessagePublisher; // Redis로 메시지를 발행하기 위한 객체


    // 발신자의 유저 이름 조회
    public String getSenderNickname(WebSocketSession session) {
        // 발신자 유저 객체 가져오기
        return (String) session.getAttributes().get("SENDER_USER_NICKNAME");
    }

    // 채널명 가져오기
    private String getChatRoomNameFromSession(WebSocketSession session) {
        // URL 쿼리 파라미터에서 채팅방 이름을 가져오는 방법
        String uri = session.getUri().toString();
        String chatRoomName = uri.substring(uri.indexOf("room=") + 5);
        return chatRoomName;
    }

    // 웹 소켓 연결이 수립될 때 호출되는 메서드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 채팅방 이름을 가져와서 해당 채널에 세션 추가
        String chatRoomName = getChatRoomNameFromSession(session);
        RedisMessageSubscriber.addSession(chatRoomName, session);
        log.info("Connection established with session: " + session.getId() + " in room: "
            + chatRoomName);
    }

    @Override
    // 웹소켓으로부터 텍스트 메시지를 수신할 때 호출되는 메시지
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
        throws Exception { // 텍스트 메시지를 처리하는 메서드
        // 수신된 메시지의 실제 메시지를 반환
        String payload = message.getPayload();
        String nickname = getSenderNickname(session);

        // 세션에서 채팅방 이름을 가져오기
        String chatRoomName = getChatRoomNameFromSession(session);

        String formattedMessage = nickname + ": " + payload; // 사용자 이름과 메시지를 조합한 문자열 생성
        log.info("Received message: " + formattedMessage); // 수신한 메시지를 콘솔에 출력

        // 포멧된 메시지를 발신자 이름으로 만들어진 채널에 발행
        redisMessagePublisher.publish(chatRoomName, formattedMessage);
    }

    @Override
    // 웹소켓 연결이 종료될 때 호출
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
        throws Exception {
        String chatRoomName = getChatRoomNameFromSession(session);
        RedisMessageSubscriber.removeSession(chatRoomName, session);
        log.info(
            "Connection closed with session: " + session.getId() + " in room: " + chatRoomName);
    }
}