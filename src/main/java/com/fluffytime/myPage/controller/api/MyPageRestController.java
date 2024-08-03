package com.fluffytime.myPage.controller.api;

import com.fluffytime.myPage.dto.MyPageResponseDto;
import com.fluffytime.myPage.dto.ProfileResponseDto;
import com.fluffytime.myPage.exception.MyPageException;
import com.fluffytime.myPage.exception.MyPageExceptionCode;
import com.fluffytime.myPage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

        MyPageResponseDto myPageResponseDto = myPageService.createMyPageResponseDto(userId,
            MyPageExceptionCode.OK);

        // 응답 dto가 null -> userId에 해당하는 마이페이지가 없을 경우
        if (myPageResponseDto == null) {
            throw new MyPageException(MyPageExceptionCode.NOT_FOUND_MYPAGE.getCode(),
                MyPageExceptionCode.NOT_FOUND_MYPAGE.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(myPageResponseDto);
    }

    // 프로필 정보 가져오기 (프로필아이디, 유저아이디, 애완동물이름, 성별, 나이, 카테고리, 프로필공개/비공개여부)
    @GetMapping("/api/mypage/profiles/info")
    public ResponseEntity<?> profilesInfo(@RequestParam("userId") String userId) {
        log.info("프로필 정보 가져오기 api 실행 " + userId);

        // 응답 dto가 null -> userId에 해당하는 프로필이 없을 경우
        ProfileResponseDto profileResponseDto = myPageService.createProfileResponseDto(userId,
            MyPageExceptionCode.OK);
        if (profileResponseDto == null) {
            throw new MyPageException(MyPageExceptionCode.NOT_FOUND_PROFILE.getCode(),
                MyPageExceptionCode.NOT_FOUND_PROFILE.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(profileResponseDto);
    }

}
