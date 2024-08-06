package com.fluffytime.mypage.controller.api;


import com.fluffytime.mypage.request.ProfileDto;
import com.fluffytime.mypage.response.CheckUsernameDto;
import com.fluffytime.mypage.response.MyPageInformationDto;
import com.fluffytime.mypage.response.ProfileInformationDto;
import com.fluffytime.mypage.response.RequestResultDto;
import com.fluffytime.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MyPageRestController {

    private final MyPageService myPageService;


    // 마이페이지 정보 가져오기
    @GetMapping("/api/mypage/info")
    public ResponseEntity<?> mypagesInfo(@RequestParam("nickname") String nickname) {
        log.info(nickname + "님의 마이페이지 정보 가져오기 api 실행 ");

        MyPageInformationDto myPageInformationDto = myPageService.createMyPageResponseDto(nickname);

        return ResponseEntity.status(HttpStatus.OK).body(myPageInformationDto);
    }

    // 프로필 정보 가져오기
    @GetMapping("/api/mypage/profiles/info")
    public ResponseEntity<?> profilesInfo(@RequestParam("nickname") String nickname) {
        log.info(nickname + "님의 프로필 정보 가져오기 api 실행 ");

        ProfileInformationDto profileInformationDto = myPageService.createProfileResponseDto(
            nickname);
        return ResponseEntity.status(HttpStatus.OK).body(profileInformationDto);
    }

    // 프로필 등록
    @PostMapping("/api/mypage/profiles/reg")
    public ResponseEntity<?> registerProfiles(@RequestParam("nickname") String nickname) {
        log.info(nickname + "님의 기본 프로필 등록 api 실행");
        RequestResultDto requestResultDto = myPageService.createProfile(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(requestResultDto);
    }

    // 프로필 정보 수정
    @PatchMapping("/api/mypage/profiles/edit")
    public ResponseEntity<?> editProfiles(
        @RequestBody ProfileDto profileDto) {
        log.info(profileDto.getNickname() + "님의 프로필 정보 수정 api 실행 ");
        RequestResultDto requestResultDto = myPageService.profileSave(profileDto);

        return ResponseEntity.status(HttpStatus.OK).body(requestResultDto);
    }

    // 프로필 유저명 중복 확인
    @GetMapping("/api/mypage/profiles/check-username")
    public ResponseEntity<?> profilesCheckUsername(@RequestParam("nickname") String nickname) {
        log.info(nickname + "가 중복 확인 api 실행");
        CheckUsernameDto checkUsernameDto = myPageService.nicknameExists(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(checkUsernameDto);
    }

    // 프로필 회원 탈퇴
    @GetMapping("/api/users/withdraw")
    public ResponseEntity<?> withdrawAccount(@RequestParam("nickname") String nickname) {
        RequestResultDto requestResultDto = myPageService.AccountDelete(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(requestResultDto);
    }
}
