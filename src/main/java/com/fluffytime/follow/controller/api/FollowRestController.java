package com.fluffytime.follow.controller.api;

import com.fluffytime.domain.User;
import com.fluffytime.follow.dto.FollowRequestDto;
import com.fluffytime.follow.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
@Slf4j
public class FollowRestController {

    private final FollowService followService;

    //팔로우
    @PostMapping("/add")
    public ResponseEntity<Void> addFollow(
        @Valid @RequestBody FollowRequestDto followRequestDto,
        HttpServletRequest httpServletRequest) {
        log.info("/api/follow/add 호출됨");

        try {
            User user = followService.findByAccessToken(httpServletRequest);
            followRequestDto.setFollowingId(user.getUserId());

            followService.follow(followRequestDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //언팔로우
    @PostMapping("/remove")
    public ResponseEntity<Void> removeFollow(
        @Valid @RequestBody FollowRequestDto followRequestDto,
        HttpServletRequest httpServletRequest) {
        log.info("/api/follow/remove 호출됨");

        try {
            User user = followService.findByAccessToken(httpServletRequest);
            followRequestDto.setFollowingId(user.getUserId());

            followService.unfollow(followRequestDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //팔로우 여부 단 건 조회
    @GetMapping("/status")
    public ResponseEntity<Boolean> getFollowStatus(
        @RequestParam("nickname") String nickname, HttpServletRequest httpServletRequest) {
        log.info("/api/follow/status 호출됨");

        try {
            User followingUser = followService.findByAccessToken(httpServletRequest);
            User followerUser = followService.findUserByNickname(nickname);

            boolean isFollowing = followService.isFollowing(followingUser.getUserId(),
                followerUser.getUserId());

            return ResponseEntity.status(HttpStatus.OK).body(isFollowing);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
