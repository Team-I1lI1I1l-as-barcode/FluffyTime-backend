package com.fluffytime.domain.chat.service;

import com.fluffytime.domain.chat.dto.response.ChatLogResponse;
import com.fluffytime.domain.chat.dto.response.ChatResponse;
import com.fluffytime.domain.chat.dto.response.ChatRoomListResponse;
import com.fluffytime.domain.chat.dto.response.RecipientResponse;
import com.fluffytime.domain.chat.entity.Chat;
import com.fluffytime.domain.chat.entity.ChatRoom;
import com.fluffytime.domain.chat.repository.ChatRoomRepository;
import com.fluffytime.domain.chat.repository.MessageRepository;
import com.fluffytime.domain.user.entity.Profile;
import com.fluffytime.domain.user.entity.ProfileImages;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.global.auth.jwt.exception.TokenNotFound;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final JwtTokenizer jwtTokenizer;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisMessagePublisher redisMessagePublisher;
    private final RedisMessageSubscriber redisMessageSubscriber;

    // 사용자 조회(userId로 조회)  메서드
    @Transactional
    public User findUserById(Long userId) {
        log.info("findUserById 실행");
        return userRepository.findById(userId).orElse(null);
    }

    // 사용자 조회(nickname으로 조회)  메서드
    @Transactional
    public User findUserByNickname(String nickname) {
        log.info("findUserByNickname 실행");
        return userRepository.findByNickname(nickname).orElse(null);
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

    // 프로필 사진 찾기
    public String findByProfileImage(String nickname) {
        User user = findUserByNickname(nickname);
        Profile profile = user.getProfile();
        String fileUrl = profile.getProfileImages().getFilePath();
        return fileUrl;
    }

    // roomID 조회
    public Long findByRoomId(String roomName) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomName(roomName).orElse(null);
        return chatRoom.getChatRoomId();
    }

    // 토픽 목록 불러오기
    public ChatRoomListResponse getTopicList(HttpServletRequest request) {
        // 로그인한 유저의 닉네임 가져오기
        User user = findByAccessToken(request);
        if (user == null) {
            // 사용자 정보가 없을 경우 적절한 예외 처리
            throw new IllegalStateException("User not found.");
        }
        String nickname = user.getNickname();
        log.info(nickname + "의 토픽 목록 불러오기!");

        // Optional 처리
        Set<String> recipient = chatRoomRepository.findAllOtherParticipants(nickname)
            .orElse(Collections.emptySet());
        Set<String> chatRoomList = chatRoomRepository.findByRoomNameContaining(nickname)
            .orElse(Collections.emptySet());

        // 프로필 사진 리스트 생성
        Set<String> profileImages = new HashSet<>();
        for (String username : recipient) {
            String fileUrl = findByProfileImage(username);
            if (fileUrl != null) {
                profileImages.add(fileUrl);
            }
        }

        // 최근 채팅 리스트 생성
        List<String> recentChatList = new ArrayList<>();
        for (String roomName : chatRoomList) {
            String recentChat = recentChatLog(roomName);
            if (recentChat != null) {
                recentChatList.add(recentChat);
            }
        }

        return new ChatRoomListResponse(recipient, chatRoomList, profileImages, recentChatList);
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

    // 수신자 정보 불러오기
    public RecipientResponse recipientInfo(String nickname) {
        User user = findUserByNickname(nickname);
        Profile profile = user.getProfile();
        ProfileImages profileImages = profile.getProfileImages();
        String fileUrl = profileImages.getFilePath();

        return RecipientResponse.builder()
            .petName(profile.getPetName())
            .nickname(nickname)
            .fileUrl(fileUrl)
            .build();
    }

    // 각 채팅방별로 마지막 채팅 내역 가져오기
    public String recentChatLog(String roomName) {
        Long chatRoomId = findByRoomId(roomName);

        // messageRepository.findByRoomId(chatRoomId)의 반환값이 Optional<List<Chat>>일 때
        Optional<List<Chat>> optionalChat = messageRepository.findByRoomId(chatRoomId);

        // Optional이 비어있지 않으며, List가 비어있지 않은 경우에만 작업 수행
        if (optionalChat.isPresent()) {
            List<Chat> chat = optionalChat.get();
            if (!chat.isEmpty()) {
                // List가 비어있지 않으므로 마지막 요소의 content 반환
                return chat.get(chat.size() - 1).getContent(); // getLast() 대신 get(size() - 1)
            }
        }
        // chat이 null이거나 빈 List인 경우
        return null;
    }

    // 채팅 내역 가져오기
    public ChatLogResponse chatLog(String roomName, HttpServletRequest request) {
        Long chatRoomId = findByRoomId(roomName);
        String sender = findByAccessToken(request).getNickname();

        // Optional<List<Chat>> 반환으로 변경
        List<Chat> chat = messageRepository.findByRoomId(chatRoomId)
            .orElse(Collections.emptyList());

        List<String> chatLog = new ArrayList<>();
        for (Chat chatMessage : chat) {
            // null 체크 및 기본값 설정
            String senderName =
                chatMessage.getSender() != null ? chatMessage.getSender() : "Unknown";
            String content =
                chatMessage.getContent() != null ? chatMessage.getContent() : "No content";
            String logEntry = senderName + " : " + content;
            chatLog.add(logEntry);
        }

        return new ChatLogResponse(roomName, sender != null ? sender : "Unknown Sender", chatLog);
    }


}
