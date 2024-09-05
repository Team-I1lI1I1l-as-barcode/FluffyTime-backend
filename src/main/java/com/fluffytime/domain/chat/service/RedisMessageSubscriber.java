package com.fluffytime.domain.chat.service;

import com.fluffytime.domain.chat.entity.Chat;
import com.fluffytime.domain.chat.repository.ChatRoomRepository;
import com.fluffytime.domain.chat.repository.MessageRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
@Slf4j
// Redis Pub/Sub 시스템에서 메시지를 수신하고, 수신된 메시지를 활성화된 웹소켓 세션에 전달하는 역할
public class RedisMessageSubscriber implements MessageListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 각 채널에 대한 세션을 관리하는 맵
    private static final Map<String, Set<WebSocketSession>> channelSessions = Collections.synchronizedMap(
        new HashMap<>());

    // 새로운 WebSocketSession을 채널의 세션 집합에 추가
    public static void addSession(String channel, WebSocketSession session) {
        synchronized (channelSessions) {
            // 채널이름에 대한 세션 집합이 존재하지 않으면 새로은 set을 생성하고 현재 세션을 추가
            channelSessions.computeIfAbsent(channel,
                k -> Collections.synchronizedSet(new HashSet<>())).add(session);
        }
    }

    // 종료된 WebSocketSession을 채널의 세션 집합에서 제거
    public static void removeSession(String channel, WebSocketSession session) {
        synchronized (channelSessions) {
            Set<WebSocketSession> sessions = channelSessions.get(channel);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    channelSessions.remove(channel);
                }
            }
        }
    }

    // 채널이름으로 채널 id 찾기
    public Long getChatRoomId(String RoomName) {
        return Objects.requireNonNull(chatRoomRepository.findByRoomName(RoomName).orElse(null))
            .getChatRoomId();
    }

    // mongodb에 채팅 내역 저장하기
    public void saveChat(Long chatRoomId, String[] temp) {
        Chat chat = new Chat();
        chat.setRoomId(chatRoomId);
        chat.setSender(temp[0]);
        chat.setContent(temp[1].trim());
        chat.setTimestamp(LocalDateTime.now());
        messageRepository.save(chat);
    }


    @Override
    // 메시지를 수신하고 처리
    public void onMessage(Message message, byte[] pattern) {
        String msg = (String) redisTemplate.getValueSerializer().deserialize(message.getBody());

        String[] temp = msg.split(":");

        String RoomName = new String(pattern);
        Long chatRoomId = getChatRoomId(RoomName);

        // 채팅 내역 저장하기
        saveChat(chatRoomId, temp);

        // 특정 채널에만 메시지 전송
        sendMessageToChannel(RoomName, msg);
    }


    // 특정 채널의 세션에만 메시지를 전송
    public void sendMessageToChannel(String channel, String msg) {
        synchronized (channelSessions) {
            Set<WebSocketSession> sessions = channelSessions.get(channel);
            if (sessions != null) {
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(new TextMessage(msg));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
