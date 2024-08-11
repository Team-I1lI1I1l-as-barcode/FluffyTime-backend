package com.fluffytime.post.dto.request;

import com.fluffytime.domain.TempStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {

    private Long tempId; // 임시 저장된 글 ID
    private String content;
    private TempStatus tempStatus;
    private List<Long> tagId = new ArrayList<>();
}
