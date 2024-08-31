package com.fluffytime.domain.chat.repository;

import com.fluffytime.domain.chat.entity.Chat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Chat, Long> {

    Optional<List<Chat>> findByRoomId(Long id);

}