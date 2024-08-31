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
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long chatRoomId;

    @Column(name = "room_name")
    String roomName; // 두명의 유저 이름의 조합으로으로 방 생성
    @Column(name = "participanta")
    String participantA; // 참가자 1
    @Column(name = "participantb")
    String participantB; // 참가자 2

    public ChatRoom(String roomName, String participantA, String participantB) {
        this.roomName = roomName;
        this.participantA = participantA;
        this.participantB = participantB;
    }
}
