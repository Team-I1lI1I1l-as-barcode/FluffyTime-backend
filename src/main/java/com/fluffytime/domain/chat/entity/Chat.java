package com.fluffytime.domain.chat.entity;


import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat")
@Getter
@Setter
// 채팅 내역을 담는 도큐먼트 - mongodb에 저장
public class Chat {

    private Long roomId; // 채널방 id
    private String sender; // 수신자
    private String content; // 메시지
    private LocalDateTime timestamp; // 메시지 발송 시간
}

