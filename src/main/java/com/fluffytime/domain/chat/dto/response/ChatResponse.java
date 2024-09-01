package com.fluffytime.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//chat 관련 서비스에 대한 클라이언트 요청 성공 여부 반환 dto
public class ChatResponse {

    private String chatRoomName; // 채널 명
    private boolean success; // 클라이언트 요청 성공 여부

}
