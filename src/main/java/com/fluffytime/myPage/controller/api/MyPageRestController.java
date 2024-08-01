package com.fluffytime.myPage.controller.api;

import com.fluffytime.myPage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MyPageRestController {

    private final MyPageService myPageService;

    // 마이페이지 정보 가져오기(닉네임, 게시물 개수, 팔로워 수, 팔로우 수)
    @GetMapping("/api/mypage/info")
    public ResponseEntity<?> mypagesInfo(@RequestParam("userId") String userId) {
        log.info("마이페이지 정보 가져오기 api 실행 " + userId);
        return ResponseEntity.ok(myPageService.createMyPageResponseDto(userId));
    }

    // 프로필 정보 가져오기 (프로필아이디, 유저아이디, 애완동물이름, 성별, 나이, 카테고리, 프로필공개/비공개여부)
    @GetMapping("/api/mypage/profiles/info")
    public ResponseEntity<?> profilesInfo(@RequestParam("userId") String userId) {
        log.info("프로필 정보 가져오기 api 실행 " + userId);
        return ResponseEntity.ok("하이");
    }

}
