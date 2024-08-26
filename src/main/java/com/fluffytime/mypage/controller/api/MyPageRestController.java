package com.fluffytime.mypage.controller.api;


import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.domain.User;
import com.fluffytime.mypage.request.ProfileDto;
import com.fluffytime.mypage.response.CheckUsernameDto;
import com.fluffytime.mypage.response.ImageResultDto;
import com.fluffytime.mypage.response.MyPageInformationDto;
import com.fluffytime.mypage.response.ProfileInformationDto;
import com.fluffytime.mypage.response.RequestResultDto;
import com.fluffytime.mypage.service.MyPageService;
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
    public ResponseEntity<MyPageInformationDto> mypagesInfo(HttpServletRequest httpServletRequest) {
        log.info("마이페이지 정보 가져오기 api 실행 ");

        User user = myPageService.findByAccessToken(httpServletRequest);
        MyPageInformationDto myPageInformationDto = myPageService.createMyPageResponseDto(
            user.getNickname());

        return ResponseEntity.status(HttpStatus.OK).body(myPageInformationDto);
    }

    // 프로필 정보 가져오기
    @GetMapping("/api/mypage/profiles/info")
    public ResponseEntity<ProfileInformationDto> profilesInfo(
        HttpServletRequest httpServletRequest) {
        log.info("프로필 정보 가져오기 api 실행 ");

        User user = myPageService.findByAccessToken(httpServletRequest);
        ProfileInformationDto profileInformationDto = myPageService.createProfileResponseDto(
            user.getNickname());
        return ResponseEntity.status(HttpStatus.OK).body(profileInformationDto);
    }


    // 프로필 정보 수정
    @PatchMapping("/api/mypage/profiles/edit")
    public ResponseEntity<RequestResultDto> editProfiles(
        @RequestBody ProfileDto profileDto) {
        log.info(profileDto.getNickname() + "님의 프로필 정보 수정 api 실행 ");

        RequestResultDto requestResultDto = myPageService.profileSave(profileDto);

        return ResponseEntity.status(HttpStatus.OK).body(requestResultDto);
    }

    // 프로필 유저명 중복 확인
    @GetMapping("/api/mypage/profiles/check-username")
    public ResponseEntity<CheckUsernameDto> profilesCheckUsername(
        @RequestParam("nickname") String nickname) {
        log.info("중복 확인 api 실행");

        CheckUsernameDto checkUsernameDto = myPageService.nicknameExists(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(checkUsernameDto);
    }

    // 프로필 이미지 업데이트
    @PatchMapping("/api/mypage/profiles/images/edit")
    public ResponseEntity<ImageResultDto> profileImagesEdit(
        @RequestParam("nickname") String nickname,
        @RequestPart("images") MultipartFile file) { // 폼데이터에서 파일을 MultipartFile로 추출
        log.info("프로필 이미지를 업데이트 api 실행");

        ImageResultDto imageResultDto = myPageService.uploadProfileImage(nickname, file);
        return ResponseEntity.status(HttpStatus.OK).body(imageResultDto);
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/api/mypage/profiles/images/delete")
    public ResponseEntity<ImageResultDto> profileImagesDelete(
        @RequestParam("nickname") String nickname) {
        log.info("프로필 이미지를 삭제 api 실행");

        ImageResultDto imageResultDto = myPageService.deleteProfileImage(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(imageResultDto);
    }

    // 프로필 회원 탈퇴
    @GetMapping("/api/users/withdraw")
    public ResponseEntity<RequestResultDto> withdrawAccount(HttpServletRequest httpServletRequest,
        HttpServletResponse response) {
        log.info("회원 탈퇴 api 실행");
        User user = myPageService.findByAccessToken(httpServletRequest);
        RequestResultDto requestResultDto = myPageService.AccountDelete(user.getNickname(),
            response);
        return ResponseEntity.status(HttpStatus.OK).body(requestResultDto);
    }


}
