package com.fluffytime.domain.chat.service;

import com.fluffytime.domain.chat.dto.response.ChatLogResponse;
import com.fluffytime.domain.chat.dto.response.ChatResponse;
import com.fluffytime.domain.chat.dto.response.ChatRoomListResponse;
import com.fluffytime.domain.chat.dto.response.RecipientInfoResponse;
import com.fluffytime.domain.chat.entity.Chat;
import com.fluffytime.domain.chat.entity.ChatRoom;
import com.fluffytime.domain.chat.exception.ChatRoomNotFound;
import com.fluffytime.domain.chat.repository.ChatRoomRepository;
import com.fluffytime.domain.chat.repository.MessageRepository;
import com.fluffytime.domain.user.entity.Profile;
import com.fluffytime.domain.user.entity.ProfileImages;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.service.MyPageService;
import com.fluffytime.global.auth.jwt.exception.TokenNotFound;
import com.fluffytime.global.common.exception.global.UserNotFound;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
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

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisMessageSubscriber redisMessageSubscriber;
    private final MyPageService myPageService;

    // 프로필 사진 찾기
    @Transactional
    public String findByProfileImage(String nickname) {
        log.info("findByProfileImage 실행");
        User user = myPageService.findUserByNickname(nickname); // 유저 객체 조회
        Profile profile = user.getProfile(); // 유저의 프로필 객체 조회
        ProfileImages profileImages = profile.getProfileImages();
        if (profileImages == null) {
            return "../../../image/profile/profile.png";
        } else {
            return profileImages.getFilePath();
        }
    }

    // roomID 조회
    @Transactional
    public Long findByRoomId(String roomName) {
        log.info("findByRoomId 실행");
        ChatRoom chatRoom = chatRoomRepository.findByRoomName(roomName).orElse(null); // 채팅방 객체 조회
        // 채팅방이 없을경우 예외 발생
        if (chatRoom == null) {
            throw new ChatRoomNotFound();
        } else {
            return chatRoom.getChatRoomId();
        }
    }

    // RecipientInfoResponse dto 생성
    public ChatRoomListResponse createResponseDto(Set<String> recipient, Set<String> chatRoomList,
        List<String> recentChatList) {
        log.info("createResponseDto 실행 >>> ChatRoomListResponse Dto 생성");
        return ChatRoomListResponse.builder()
            .recipient(recipient)
            .chatRoomList(chatRoomList)
            .recentChat(recentChatList)
            .build();
    }

    // nickname(로그인한 유저)을 기준으로 모든 채팅방에서 나와 채팅중인 사람을 찾아 Set에 저장
    @Transactional
    public Set<String> findRecipientList(String nickname) {
        log.info("findRecipientList 실행");
        // 수신자가 없다면 null 값 반환
        return chatRoomRepository.findAllOtherParticipants(nickname)
            .orElse(null);
    }

    // nickname(로그인한 유저)을 기준으로 본인이 속한 채팅방을 찾아 Set에 저장
    @Transactional
    public Set<String> findChatRoomList(String nickname) {
        log.info("findChatRoomList 실행");
        // 채팅방이 없다면 null 값 반환
        return chatRoomRepository.findByRoomNameContaining(nickname)
            .orElse(null);
    }


    // nickname(로그인한 유저)을 기준으로 본인이 속한 모든 채팅방의 최신 채팅 내역을 찾아  List에 저장
    @Transactional
    public List<String> findChatLog(Set<String> chatRoomList) {
        log.info("findChatLog 실행");
        List<String> recentChatList = new ArrayList<>();
        // 채팅방이 있는경우 최신 내역 담기
        if (chatRoomList != null) {
            for (String roomName : chatRoomList) {
                String recentChat = recentChatLog(roomName);
                recentChatList.add(recentChat);
            }
        }
        return recentChatList;
    }

    // 각 채팅방별로 마지막 채팅 내역 가져오기
    @Transactional
    public String recentChatLog(String roomName) {
        log.info("recentChatLog 실행");
        Long chatRoomId = findByRoomId(roomName);
        Optional<List<Chat>> optionalChat = messageRepository.findByRoomId(chatRoomId);

        if (optionalChat.isPresent()) {
            List<Chat> chat = optionalChat.get();
            if (!chat.isEmpty()) {
                // List가 비어있지 않으므로 마지막 요소의 content 반환
                return chat.get(chat.size() - 1).getContent(); // getLast() 대신 get(size() - 1)
            }
        }
        return " ";
    }

    // 모든 채팅 내역 가져오기
    @Transactional
    public ChatLogResponse chatLog(String roomName, HttpServletRequest request) {
        log.info("chatLog 실행");
        List<String> chatLog = new ArrayList<>();
        Long chatRoomId = findByRoomId(roomName);
        User user = myPageService.findByAccessToken(request);
        if (user == null) {
            throw new TokenNotFound();
        }
        String sender = user.getNickname();

        // 채팅 내역을 담은 객체 가져오기
        List<Chat> chat = messageRepository.findByRoomId(chatRoomId)
            .orElse(null);

        if (chat != null) {
            for (Chat chatMessage : chat) {
                String senderName = chatMessage.getSender();
                String content = chatMessage.getContent();
                String logEntry = senderName + " : " + content;
                chatLog.add(logEntry);
            }
        }
        return createResponseDto(roomName, sender, chatLog);
    }

    // 토픽 목록 불러오기
    @Transactional
    public ChatRoomListResponse getTopicList(HttpServletRequest request) {
        log.info("getTopicList 실행");
        // 로그인한 유저의 닉네임 가져오기
        User user = myPageService.findByAccessToken(request);
        if (user == null) {
            throw new TokenNotFound();
        }

        String nickname = user.getNickname();
        log.info(nickname + "의 토픽 목록 불러오기!");

        // nickname(로그인한 유저)과 채팅중인 모든 수신자을 담은  Set
        Set<String> recipient = findRecipientList(nickname);

        // nickname(로그인한 유저)이 속한 모든 채널방 이름을 담은  Set
        Set<String> chatRoomList = findChatRoomList(nickname);

        //nickname(로그인한 유저)의 속한 모든 채팅방의 최근 채팅 내역을 담은 List
        List<String> recentChatList = findChatLog(chatRoomList);

        return createResponseDto(recipient, chatRoomList, recentChatList);
    }

    // RecipientInfoResponse dto 생성
    @Transactional
    public ChatResponse createResponseDto(String chatRoomName, boolean success) {
        log.info("createResponseDto 실행 >>> ChatResponse Dto 생성");
        return ChatResponse.builder()
            .chatRoomName(chatRoomName)
            .success(success)
            .build();
    }

    // 채널 생성
    @Transactional
    public String creatChatRoom(String[] users) {
        log.info("creatChatRoom 실행");
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
        return chatRoomName;
    }

    // 토픽 생성하기
    @Transactional
    public ChatResponse createTopic(String user1, HttpServletRequest request) {
        log.info("createTopic 실행");
        // 알파벳 순서대로 정렬
        User user = myPageService.findByAccessToken(request);
        String[] users;
        if (user != null) {
            String user2 = user.getNickname();
            users = new String[]{user2, user1};
            Arrays.sort(users);
        } else {
            throw new UserNotFound();
        }
        return createResponseDto(creatChatRoom(users), true);
    }

    // 토픽 참여하기
    public ChatResponse JoinTopic(String roomName) {
        log.info("JoinTopic 실행");
        ChannelTopic channelTopic = new ChannelTopic(roomName);
        redisMessageListenerContainer.addMessageListener(redisMessageSubscriber, channelTopic);
        return new ChatResponse(roomName, true);

    }

    // RecipientInfoResponse dto 생성
    public RecipientInfoResponse createResponseDto(Profile profile, String nickname,
        String fileUrl) {
        log.info("createResponseDto 실행 >>> RecipientInfoResponse Dto 생성");
        return RecipientInfoResponse.builder()
            .petName(profile.getPetName())
            .nickname(nickname)
            .fileUrl(fileUrl)
            .build();
    }

    // 수신자 정보 불러오기
    @Transactional
    public RecipientInfoResponse recipientInfo(String nickname) {
        log.info("RecipientInfoResponse 실행");
        User user = myPageService.findUserByNickname(nickname);
        Profile profile = user.getProfile();
        String fileUrl = findByProfileImage(nickname);
        return createResponseDto(profile, nickname, fileUrl);
    }

    // ChatLogResponse dto 생성
    public ChatLogResponse createResponseDto(String roomName, String sender, List<String> chatLog) {
        log.info("createResponseDto 실행 >>> ChatLogResponse Dto 생성");
        return ChatLogResponse.builder()
            .roomName(roomName)
            .sender(sender)
            .chatLog(chatLog)
            .build();
    }
}
