package com.fluffytime.domain.chat.repository;

import com.fluffytime.domain.chat.entity.Chat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Chat, Long> {

    // 채널 id로 해당 채팅방에서 나눈 채팅 내역 리스트
    Optional<List<Chat>> findByRoomId(Long id);

}