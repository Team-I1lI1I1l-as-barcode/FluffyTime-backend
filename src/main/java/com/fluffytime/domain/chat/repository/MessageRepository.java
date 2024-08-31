package com.fluffytime.domain.chat.repository;

import com.fluffytime.domain.chat.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
//
//public interface MessageRepository extends JpaRepository<MessageSave, Long> {
//
//}


public interface MessageRepository extends MongoRepository<Chat, Long> {

}