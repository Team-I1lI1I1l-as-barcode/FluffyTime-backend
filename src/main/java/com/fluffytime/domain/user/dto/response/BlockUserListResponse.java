package com.fluffytime.domain.user.dto.response;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BlockUserListResponse {

    private List<Map<String, String>> blockUserList; // 차단 유저 리스트(사용자 명, 프로필 사진 URL)
}
