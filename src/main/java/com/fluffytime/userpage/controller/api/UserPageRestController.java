package com.fluffytime.userpage.controller.api;

import com.fluffytime.userpage.response.BlockUserListDto;
import com.fluffytime.userpage.response.UserBlockResponseDto;
import com.fluffytime.userpage.response.UserPageInformationDto;
import com.fluffytime.userpage.service.UserPageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserPageRestController {

    private final UserPageService userPageService;


    // 유저 페이지 정보 가져오기
    @GetMapping("/api/users/pages")
    public ResponseEntity<UserPageInformationDto> userPages(
        @RequestParam("nickname") String nickname,
        HttpServletRequest httpServletRequest) {
        log.info("유저페이지 정보 가져오기 api 실행");
        UserPageInformationDto userPageInformationDto = userPageService.createUserPageInformationDto(
            nickname, httpServletRequest);

        return ResponseEntity.status(HttpStatus.OK).body(userPageInformationDto);
    }

    // 유저 차단
    @PostMapping("/api/users/block")
    public ResponseEntity<UserBlockResponseDto> blockUser(@RequestParam("nickname") String nickname,
        HttpServletRequest httpServletRequest) {
        log.info("유저 차단");
        UserBlockResponseDto userBlockResponseDto = userPageService.userBlock(nickname,
            httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userBlockResponseDto);
    }

    // 유저 차단 해제
    @DeleteMapping("/api/users/unblock")
    public ResponseEntity<UserBlockResponseDto> unblockUser(
        @RequestParam("nickname") String nickname,
        HttpServletRequest httpServletRequest) {
        log.info("유저 차단 해제");
        UserBlockResponseDto userBlockResponseDto = userPageService.removeUserBlock(nickname,
            httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userBlockResponseDto);
    }

    // 유저 차단 목록 가져오기
    @GetMapping("/api/users/block/list")
    public ResponseEntity<BlockUserListDto> blockUserList(HttpServletRequest httpServletRequest) {
        log.info("유저 차단 리스트 불러오기");
        BlockUserListDto blockUserListDto = userPageService.blockUserList(httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(blockUserListDto);
    }

}
