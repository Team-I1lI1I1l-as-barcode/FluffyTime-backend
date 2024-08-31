package com.fluffytime.domain.chat.repository;

import com.fluffytime.domain.chat.entity.ChatRoom;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByRoomName(String roomName);

    Optional<ChatRoom> findByRoomName(String roodName);

    @Query("SELECT c.roomName FROM chat_room c WHERE c.roomName LIKE %:nickname%")
    Optional<Set<String>> findByRoomNameContaining(@Param("nickname") String nickname);

    @Query(
        "SELECT CASE WHEN c.participantA = :nickname THEN c.participantB ELSE c.participantA END " +
            "FROM chat_room c " +
            "WHERE c.participantA = :nickname OR c.participantB = :nickname")
    Optional<Set<String>> findAllOtherParticipants(@Param("nickname") String nickname);


}
