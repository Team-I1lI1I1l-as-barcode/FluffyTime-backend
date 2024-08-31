package com.fluffytime.domain.chat.entity;


import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


//@Entity(name = "message_save")
@Document(collection = "chat")
@Getter
@Setter
public class Chat {

    private Long roomId; // 채널 id
    private String sender; // 수신자
    private String content; // 메시지
    private LocalDateTime timestamp;

    @PrePersist
    public void create() {
        this.timestamp = LocalDateTime.now();
    }
}

