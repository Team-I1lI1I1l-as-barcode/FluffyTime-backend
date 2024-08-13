package com.fluffytime.search.controller.api;

import com.fluffytime.domain.Profile;
import com.fluffytime.domain.User;
import com.fluffytime.search.dto.request.SearchRequestDto;
import com.fluffytime.search.service.SearchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/search")
public class SearchRestController {

    private final SearchService searchService;

    @PostMapping("/names")
    public ResponseEntity<Map<String, Object>> searchNames(
        @RequestBody SearchRequestDto requestDto
        /*,
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "perPage", required = false, defaultValue = "24") int perPage*/) {
        log.info("반려동물 이름 기반 검색실행. 검색어: {}", requestDto);

        try {
            List<Map<String, String>> list = new ArrayList<>(); // TODO responseDto로 변환??

            // db로부터 게시물 리스트 받아오기
            List<Profile> profileList = searchService.findMatchingPetName(requestDto);

//        // 페이징 처리
//        int start = (page - 1) * perPage;
//        int end = Math.min(start + perPage, userList.size());

//        for (int i = start; i <= end; i++) {//TODO 무한 스크롤 페이징 처리
            for (int i = 0; i <= 12; i++) {

                if (i >= profileList.size()) {
                    break;
                }

                Profile profile = profileList.get(i);

                Map<String, String> item = new HashMap<>();
                item.put("nickName", profile.getUser().getNickname());
                item.put("petName", profile.getPetName());
                String imageUrl = "https://via.placeholder.com/140";
                if (profile.getProfileImages() != null) {
                    imageUrl = profile.getProfileImages().getFilePath();
                }
                item.put("imageUrl", imageUrl);

                list.add(item);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("list", list);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error processing request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);//TODO 공통 양식처럼 익셉션 처리하기
        }

    }

    @PostMapping("/tags")
    public ResponseEntity<Map<String, Object>> searchTags(
        @RequestBody SearchRequestDto requestDto) {

        List<Map<String, String>> list = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("tagId", String.valueOf(i));
            item.put("tagName", "Item " + i);
            item.put("imageUrl", "https://via.placeholder.com/150");
            list.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);

        return ResponseEntity.ok(response);

    }


    @PostMapping("/accounts")
    public ResponseEntity<Map<String, Object>> searchAccounts(
        @RequestBody SearchRequestDto requestDto) {

        log.info("유저 닉네임 기반 검색실행. 검색어: {}", requestDto);

        try {
            List<Map<String, String>> list = new ArrayList<>();// TODO responseDto로 변환??

            // db로부터 게시물 리스트 받아오기
            List<User> userList = searchService.findMatchingUsers(requestDto);

//        // 페이징 처리
//        int start = (page - 1) * perPage;
//        int end = Math.min(start + perPage, userList.size());

//        for (int i = start; i <= end; i++) {//TODO 무한 스크롤 페이징 처리
            for (int i = 0; i <= 12; i++) {

                if (i >= userList.size()) {
                    break;
                }

                User user = userList.get(i);

                Map<String, String> item = new HashMap<>();
                item.put("nickName", user.getNickname());

                Profile profile = user.getProfile();
                if (profile != null && profile.getPetName() != null) {
                    item.put("petName", profile.getPetName());
                } else {
                    item.put("petName", "없음");
                }

                String imageUrl = "https://via.placeholder.com/130";
                if (profile != null && profile.getProfileImages() != null) {
                    imageUrl = profile.getProfileImages().getFilePath();
                }
                item.put("imageUrl", imageUrl);
                list.add(item);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("list", list);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error processing request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);//TODO 공통 양식처럼 익셉션 처리하기
        }

    }


}
