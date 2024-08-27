package com.fluffytime.domain.board.dto.request;

import com.fluffytime.domain.board.entity.enums.TempStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {

    private Long tempId; // 임시 저장된 글 ID
    private String content;
    private TempStatus tempStatus;

}
