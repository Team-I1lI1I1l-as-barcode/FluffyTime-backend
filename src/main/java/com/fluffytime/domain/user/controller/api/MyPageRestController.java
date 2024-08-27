package com.fluffytime.domain.user.controller.api;


import com.fluffytime.domain.user.dto.request.ProfileRequest;
import com.fluffytime.domain.user.dto.response.CheckUsernameResponse;
import com.fluffytime.domain.user.dto.response.ImageResultResponse;
import com.fluffytime.domain.user.dto.response.MyPageInformationResponse;
import com.fluffytime.domain.user.dto.response.ProfileInformationResponse;
import com.fluffytime.domain.user.dto.response.RequestResultResponse;
import com.fluffytime.domain.user.service.MyPageService;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MyPageRestController {

    private final MyPageService myPageService;
    private final JwtTokenizer jwtTokenizer;

    // 마이페이지 정보 가져오기
    @GetMapping("/api/mypage/info")
    public ResponseEntity<MyPageInformationResponse> mypagesInfo(HttpServletRequest httpServletRequest) {
        log.info("마이페이지 정보 가져오기 api 실행 ");

        User user = myPageService.findByAccessToken(httpServletRequest);
        MyPageInformationResponse myPageInformationResponse = myPageService.createMyPageResponseDto(
            user.getNickname());

        return ResponseEntity.status(HttpStatus.OK).body(myPageInformationResponse);
    }

    // 프로필 정보 가져오기
    @GetMapping("/api/mypage/profiles/info")
    public ResponseEntity<ProfileInformationResponse> profilesInfo(
        HttpServletRequest httpServletRequest) {
        log.info("프로필 정보 가져오기 api 실행 ");

        User user = myPageService.findByAccessToken(httpServletRequest);
        ProfileInformationResponse profileInformationResponse = myPageService.createProfileResponseDto(
            user.getNickname());
        return ResponseEntity.status(HttpStatus.OK).body(profileInformationResponse);
    }


    // 프로필 정보 수정
    @PatchMapping("/api/mypage/profiles/edit")
    public ResponseEntity<RequestResultResponse> editProfiles(
        @RequestBody ProfileRequest profileRequest) {
        log.info(profileRequest.getNickname() + "님의 프로필 정보 수정 api 실행 ");

        RequestResultResponse requestResultResponse = myPageService.profileSave(profileRequest);

        return ResponseEntity.status(HttpStatus.OK).body(requestResultResponse);
    }

    // 프로필 유저명 중복 확인
    @GetMapping("/api/mypage/profiles/check-username")
    public ResponseEntity<CheckUsernameResponse> profilesCheckUsername(
        @RequestParam("nickname") String nickname) {
        log.info("중복 확인 api 실행");

        CheckUsernameResponse checkUsernameResponse = myPageService.nicknameExists(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(checkUsernameResponse);
    }

    // 프로필 이미지 업데이트
    @PatchMapping("/api/mypage/profiles/images/edit")
    public ResponseEntity<ImageResultResponse> profileImagesEdit(
        @RequestParam("nickname") String nickname,
        @RequestPart("images") MultipartFile file) { // 폼데이터에서 파일을 MultipartFile로 추출
        log.info("프로필 이미지를 업데이트 api 실행");

        ImageResultResponse imageResultResponse = myPageService.uploadProfileImage(nickname, file);
        return ResponseEntity.status(HttpStatus.OK).body(imageResultResponse);
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/api/mypage/profiles/images/delete")
    public ResponseEntity<ImageResultResponse> profileImagesDelete(
        @RequestParam("nickname") String nickname) {
        log.info("프로필 이미지를 삭제 api 실행");

        ImageResultResponse imageResultResponse = myPageService.deleteProfileImage(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(imageResultResponse);
    }

    // 프로필 회원 탈퇴
    @GetMapping("/api/users/withdraw")
    public ResponseEntity<RequestResultResponse> withdrawAccount(HttpServletRequest httpServletRequest,
        HttpServletResponse response) {
        log.info("회원 탈퇴 api 실행");
        User user = myPageService.findByAccessToken(httpServletRequest);
        RequestResultResponse requestResultResponse = myPageService.AccountDelete(user.getNickname(),
            response);
        return ResponseEntity.status(HttpStatus.OK).body(requestResultResponse);
    }


}
