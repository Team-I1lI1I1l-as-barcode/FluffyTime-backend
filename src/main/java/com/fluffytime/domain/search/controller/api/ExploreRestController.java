package com.fluffytime.domain.search.controller.api;

import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.board.entity.PostImages;
import com.fluffytime.domain.board.entity.enums.TempStatus;
import com.fluffytime.domain.search.service.ExploreService;
import com.fluffytime.domain.user.entity.Profile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ExploreRestController {

    private final ExploreService exploreService;

    @Transactional
    @GetMapping("/api/explore")
    public ResponseEntity<Map<String, Object>> findExplore(
        // 메서드명에 get을 사용할 때는 무조건 데이터가 존재할 경우에만, 없어도 되는 경우에는 find 사용(+optional 사용)
        @RequestParam(value = "tag", required = false) String tag,
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "perPage", required = false, defaultValue = "24") int perPage) {

        // db로부터 게시물 리스트 받아오기
        List<Post> posts = exploreService.findLatestPosts();

        // 페이징 처리
        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, posts.size());

        //TODO 태그에 따라서 클라이언트로 보낼 게시물 리스트 생성방식 다르게 하기
//        int end = 24;
//        if (tag != null && !tag.isEmpty()) {
//            end = 11;
//        }

        //태그 기반으로 제대로 탐색됐는지 확인
        log.info("Getting explore for tag {}", tag);
        log.info("Getting explore for end {}", end);

        // 클라이언트로 보낼 게시물 리스트 생성
        List<Map<String, String>> list = new ArrayList<>();

        // 데이터 기반으로 리스트 생성
        for (int i = start; i < end; i++) {

            if (i >= posts.size()) {
                break;
            }

            Post post = posts.get(i);
            if (post.getTempStatus() == TempStatus.TEMP) {
                log.info("임시저장글 숨김!");
                continue;
            }

            Map<String, String> item = new HashMap<>();
            item.put("postId", post.getPostId().toString());
            item.put("userId", post.getUser().getUserId().toString());
            item.put("nickname", post.getUser().getNickname());
            item.put("content", post.getContent());
            item.put("createdAt", post.getCreatedAt().toString());
            //가장 첫번째 사진 url 불러오기
            String imageUrl = post.getPostImages()
                .stream()
                .findFirst()
                .map(PostImages::getFilepath)
                .orElse(null);

            item.put("imageUrl", imageUrl);

            String profileImageUrl = "/image/profile/profile.png";
//            위에 프로필 사진 없는 경우 기본 이미지 넣기

            Profile profile = post.getUser().getProfile();
            if (profile != null && profile.getProfileImages() != null) {
                profileImageUrl = profile.getProfileImages().getFilePath();
            }

            item.put("profileImageUrl", profileImageUrl);

            list.add(item);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("list", list);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
