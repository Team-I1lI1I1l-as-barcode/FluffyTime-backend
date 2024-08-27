package com.fluffytime.domain.user.controller.api;

import com.fluffytime.domain.user.dto.response.BlockUserListResponse;
import com.fluffytime.domain.user.dto.response.UserBlockResponse;
import com.fluffytime.domain.user.dto.response.UserPageInformationResponse;
import com.fluffytime.domain.user.service.UserPageService;
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
    public ResponseEntity<UserPageInformationResponse> userPages(
        @RequestParam("nickname") String nickname,
        HttpServletRequest httpServletRequest) {
        log.info("유저페이지 정보 가져오기 api 실행");
        UserPageInformationResponse userPageInformationResponse = userPageService.createUserPageInformationDto(
            nickname, httpServletRequest);

        return ResponseEntity.status(HttpStatus.OK).body(userPageInformationResponse);
    }

    // 유저 차단
    @PostMapping("/api/users/block")
    public ResponseEntity<UserBlockResponse> blockUser(@RequestParam("nickname") String nickname,
        HttpServletRequest httpServletRequest) {
        log.info("유저 차단");
        UserBlockResponse userBlockResponse = userPageService.userBlock(nickname,
            httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userBlockResponse);
    }

    // 유저 차단 해제
    @DeleteMapping("/api/users/unblock")
    public ResponseEntity<UserBlockResponse> unblockUser(
        @RequestParam("nickname") String nickname,
        HttpServletRequest httpServletRequest) {
        log.info("유저 차단 해제");
        UserBlockResponse userBlockResponse = userPageService.removeUserBlock(nickname,
            httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userBlockResponse);
    }

    // 유저 차단 목록 가져오기
    @GetMapping("/api/users/block/list")
    public ResponseEntity<BlockUserListResponse> blockUserList(HttpServletRequest httpServletRequest) {
        log.info("유저 차단 리스트 불러오기");
        BlockUserListResponse blockUserListResponse = userPageService.blockUserList(httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(blockUserListResponse);
    }

}
