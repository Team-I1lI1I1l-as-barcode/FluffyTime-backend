package com.fluffytime.domain.chat.service;

import com.fluffytime.domain.chat.dto.response.ChatResponse;
import com.fluffytime.domain.chat.dto.response.ChatRoomListResponse;
import com.fluffytime.domain.chat.entity.ChatRoom;
import com.fluffytime.domain.chat.repository.ChatRoomRepository;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.global.auth.jwt.exception.TokenNotFound;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServcie {

    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisMessagePublisher redisMessagePublisher;
    private final RedisMessageSubscriber redisMessageSubscriber;

    // 사용자 조회(userId로 조회)  메서드
    @Transactional
    public User findUserById(Long userId) {
        log.info("findUserById 실행");
        return userRepository.findById(userId).orElse(null);
    }

    // accessToken 토큰으로 사용자 찾기  메서드
    @Transactional
    public User findByAccessToken(HttpServletRequest httpServletRequest) {
        log.info("findByAccessToken 실행");
        String accessToken = jwtTokenizer.getTokenFromCookie(httpServletRequest, "accessToken");
        if (accessToken == null) {
            throw new TokenNotFound();
        }
        // accessToken값으로  UserId 추출
        Long userId = Long.valueOf(
            ((Integer) jwtTokenizer.parseAccessToken(accessToken).get("userId")));
        // id(pk)에 해당되는 사용자 추출
        return findUserById(userId);
    }

    // 토픽 목록 불러오기
    public ChatRoomListResponse getTopicList(HttpServletRequest request) {
        String nickname = findByAccessToken(request).getNickname(); // 로그인한 유저 nickname
        log.info(nickname + "의 토픽 목록 불러오기!");
        Set<String> recipient = chatRoomRepository.findAllOtherParticipants(nickname);
        Set<String> chatRoomList = chatRoomRepository.findByRoomNameContaining(nickname);

        return new ChatRoomListResponse(recipient, chatRoomList);
    }

    // 토픽 생성하기
    public ChatResponse createTopic(String nickname, HttpServletRequest request) {
        // 알파벳 순서대로 정렬
        String[] users = {findByAccessToken(request).getNickname(), nickname};
        Arrays.sort(users);

        // 채널 명 생성
        String chatRoomName = "chat_" + users[0] + "_" + users[1];
        // 채널 생성
        if (!chatRoomRepository.existsByRoomName(
            chatRoomName)) { // DB에 해당 방 정보가 없을 경우 추가
            ChannelTopic channelTopic = new ChannelTopic(chatRoomName);
            // 수신된 메시지 처리하기 위해 메시지 리스너를 등록
            redisMessageListenerContainer.addMessageListener(redisMessageSubscriber, channelTopic);
            log.info("새로운 채팅방을 개설합니다.");
            ChatRoom chatRoom = new ChatRoom(chatRoomName, users[0],
                users[1]); // 채널 이름, 참가자1, 참가자2
            chatRoomRepository.save(chatRoom);
        } else {
            log.info("기존에 있던 채팅방을 사용합니다.");
        }
        return new ChatResponse(chatRoomName, true);
    }

    // 토픽 참여하기
    public ChatResponse JoinTopic(String roomName) {
        ChannelTopic channelTopic = new ChannelTopic(roomName);
        redisMessageListenerContainer.addMessageListener(redisMessageSubscriber, channelTopic);
        return new ChatResponse(roomName, true);

    }

}
