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

    private Set<String> recipient; // 수신자
    private Set<String> chatRoomList; // 채널 이름
    private Set<String> profileImages; // 수신자 프로필 사진

}
