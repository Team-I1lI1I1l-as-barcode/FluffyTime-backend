package com.fluffytime.domain.chat.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "chat_room")
// 채팅방에 대한 정보를 담는 엔티티 - mysql에 저장
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long chatRoomId;

    @Column(name = "room_name", nullable = false)
    String roomName; // 두명의 유저 이름의 조합으로으로 방 생성

    @Column(name = "participanta", nullable = false)
    String participantA; // 참가자 1

    @Column(name = "participantb", nullable = false)
    String participantB; // 참가자 2

    public ChatRoom(String roomName, String participantA, String participantB) {
        this.roomName = roomName;
        this.participantA = participantA;
        this.participantB = participantB;
    }
}
