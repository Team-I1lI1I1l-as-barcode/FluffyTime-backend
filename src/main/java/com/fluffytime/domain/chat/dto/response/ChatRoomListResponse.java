package com.fluffytime.domain.chat.dto.response;

import java.util.List;
import java.util.Set;
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
// 채널 목록에서 사용될 채널에 관련된 내용을 담는 dto
public class ChatRoomListResponse {

    private Set<String> recipient; // 수신자
    private Set<String> chatRoomList; // 채널 이름
    private List<String> recentChat; // 각 채널별 최근 메시지
}
