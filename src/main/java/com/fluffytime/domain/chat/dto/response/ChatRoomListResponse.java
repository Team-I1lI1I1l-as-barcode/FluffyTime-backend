package com.fluffytime.domain.chat.dto.response;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListResponse {

    private Set<String> recipient; // 수신이
    private Set<String> chatRoomList; // 채널 이름

}
