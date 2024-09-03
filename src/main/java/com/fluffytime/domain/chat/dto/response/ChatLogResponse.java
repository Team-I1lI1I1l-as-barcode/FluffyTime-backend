package com.fluffytime.domain.chat.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// 채팅 내역을 담은 dto
public class ChatLogResponse {

    private String roomName; // 채널이름
    private String sender;  // 발신자
    private List<String> chatLog; // 채팅 내역 리스트

}
