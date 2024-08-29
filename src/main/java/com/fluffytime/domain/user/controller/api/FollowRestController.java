package com.fluffytime.domain.user.controller.api;

import com.fluffytime.domain.user.dto.request.FollowRequest;
import com.fluffytime.domain.user.dto.response.FollowCountResponse;
import com.fluffytime.domain.user.dto.response.FollowListResponse;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        @Valid @RequestBody FollowRequest followRequest,
        HttpServletRequest httpServletRequest) {
        log.info("/api/follow/add 호출됨");

        try {
            User user = followService.findByAccessToken(httpServletRequest);
            followRequest.setFollowingId(user.getUserId());

            followService.follow(followRequest);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //언팔로우
    @PostMapping("/remove")
    public ResponseEntity<Void> removeFollow(
        @Valid @RequestBody FollowRequest followRequest,
        HttpServletRequest httpServletRequest) {
        log.info("/api/follow/remove 호출됨");

        try {
            User user = followService.findByAccessToken(httpServletRequest);
            followRequest.setFollowingId(user.getUserId());

            followService.unfollow(followRequest);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //팔로우 여부 단 건 조회
    @GetMapping("/status")
    public ResponseEntity<Boolean> getFollowStatus(
        @RequestParam(name = "nickname") String nickname, HttpServletRequest httpServletRequest) {
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

    // 팔로워 및 팔로잉 수 조회
    @GetMapping("/count/{nickname}")
    public ResponseEntity<FollowCountResponse> getFollowCounts(
        @PathVariable(name = "nickname") String nickname) {
        log.info("/api/follow/count/{} 호출됨", nickname);

        try {
            FollowCountResponse followCountResponse = followService.getFollowCounts(nickname);
            return ResponseEntity.status(HttpStatus.OK).body(followCountResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //팔로워 목록 조회
    @GetMapping("/search/followers/{nickname}")
    public ResponseEntity<List<FollowListResponse>> findFollowers(
        @PathVariable(name = "nickname") String nickname) {
        log.info("/api/follow/search/followers/{} 호출됨", nickname);

        try {
            User user = followService.findUserByNickname(nickname);

            // 대상 유저의 팔로워 목록 가져오기
            List<FollowListResponse> followListResponses = followService.findFollowersByUserId(
                user.getUserId());

            return ResponseEntity.status(HttpStatus.OK).body(followListResponses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //팔로잉 목록 조회
    @GetMapping("/search/followings/{nickname}")
    public ResponseEntity<List<FollowListResponse>> findFollowings(
        @PathVariable(name = "nickname") String nickname) {
        log.info("/api/follow/search/followings/{} 호출됨", nickname);

        try {
            User user = followService.findUserByNickname(nickname);

            // 대상 유저의 팔로잉 목록 가져오기
            List<FollowListResponse> followListResponses = followService.findFollowingsByUserId(
                user.getUserId());

            return ResponseEntity.status(HttpStatus.OK).body(followListResponses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
