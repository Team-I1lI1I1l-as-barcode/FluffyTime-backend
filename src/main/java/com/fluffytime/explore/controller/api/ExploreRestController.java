package com.fluffytime.explore.controller.api;

import com.fluffytime.domain.Post;
import com.fluffytime.explore.service.ExploreService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ExploreRestController {

    private final ExploreService exploreService;

    @GetMapping("/api/explore")
    public ResponseEntity<Map<String, Object>> findExplore(
        // 메서드명에 get을 사용할 때는 무조건 데이터가 존재할 경우에만, 없어도 되는 경우에는 find 사용(+optional 사용)
        @RequestParam(value = "tag", required = false) String tag) {

        // db로부터 데이터 받아오기
        List<Post> posts = exploreService.findLatestPosts();
        List<Map<String, String>> list = new ArrayList<>();

        int end = 9;
        if (tag != null && !tag.isEmpty()) {
            end = 5;
        }

        //태그 기반으로 제대로 탐색됐는지 확인
        log.info("Getting explore for tag {}", tag);
        log.info("Getting explore for end {}", end);

        // 데이터 기반으로 리스트 생성
        for (int i = 0; i < end; i++) {
            Post post = posts.get(i);
            Map<String, String> item = new HashMap<>();
            item.put("postId", String.valueOf(post.getPostId()));
            item.put("imageUrl", "https://via.placeholder.com/150");//TODO 실제 이미지 url로 수정 필요
            list.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);

        return ResponseEntity.ok(response);
    }

}
