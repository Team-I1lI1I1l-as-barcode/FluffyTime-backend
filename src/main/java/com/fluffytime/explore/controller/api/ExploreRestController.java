package com.fluffytime.explore.controller.api;

import com.fluffytime.domain.Post;
import com.fluffytime.explore.service.ExploreService;
import java.util.ArrayList;
import java.util.Comparator;
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

        // db로부터 게시물 리스트 받아오기
        List<Post> posts = exploreService.findLatestPosts();

        //최신날짜 순으로 정렬
        posts.sort(Comparator.comparing(Post::getCreatedAt).reversed());

        //TODO 태그에 따라서 클라이언트로 보낼 게시물 리스트 생성방식 다르게 하기
        int end = 9;
        if (tag != null && !tag.isEmpty()) {
            end = 8;
        }

        //태그 기반으로 제대로 탐색됐는지 확인
        log.info("Getting explore for tag {}", tag);
        log.info("Getting explore for end {}", end);

        // 클라이언트로 보낼 게시물 리스트 생성
        List<Map<String, String>> list = new ArrayList<>();

        // 데이터 기반으로 리스트 생성
        for (int i = 0; i < end; i++) {

            if (i >= posts.size()) {
                break;
            }

            Post post = posts.get(i);
            Map<String, String> item = new HashMap<>();
            item.put("postId", post.getPostId().toString());
            item.put("userId", post.getUser().getUserId().toString());
            item.put("content", post.getContent());
            item.put("createdAt", post.getCreatedAt().toString());
            item.put("imageUrl", "https://via.placeholder.com/150");//TODO 실제 이미지 url로 수정 필요
            //item.put("imageUrl", post.getImageUrls.~가장 첫번째 사진 url 불러오기
            list.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);

        return ResponseEntity.ok(response);
    }

}
