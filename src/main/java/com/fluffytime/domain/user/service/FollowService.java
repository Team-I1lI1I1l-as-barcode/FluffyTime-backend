package com.fluffytime.domain.user.service;

import com.fluffytime.domain.notification.service.NotificationService;
import com.fluffytime.domain.user.dto.request.FollowRequest;
import com.fluffytime.domain.user.dto.response.FollowCountResponse;
import com.fluffytime.domain.user.dto.response.FollowListResponse;
import com.fluffytime.domain.user.entity.Follow;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.exception.FollowNotFound;
import com.fluffytime.domain.user.exception.SelfFollowRequest;
import com.fluffytime.domain.user.repository.FollowRepository;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.global.common.exception.global.UserNotFound;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;
    private final NotificationService notificationService;

    // 팔로우 여부 단 건 확인 메서드
    public boolean isFollowing(Long followingId, Long followedId) {
        return followRepository.findByFollowingUserUserIdAndFollowedUserUserId(followingId,
            followedId).isPresent();
    }

    //팔로우 등록
    @Transactional
    public void follow(FollowRequest followRequest) {

        // User를 db에서 조회
        User followingUser = userRepository.findById(followRequest.getFollowingId())
            .orElseThrow(UserNotFound::new);
        User followedUser = userRepository.findByNickname(
                followRequest.getFollowedUserNickname())
            .orElseThrow(UserNotFound::new);

        //본인을 팔로우하는것을 막음
        if (followedUser == followingUser) {
            throw new SelfFollowRequest();
        }

        // Follow 엔티티로 변환
        Follow follow = new Follow();
        follow.setFollowingUser(followingUser);
        follow.setFollowedUser(followedUser);

        followRepository.save(follow);

        // 알림 생성 및 전송
        notificationService.createFollowNotification(followingUser, followedUser);
    }

    //언팔로우 (팔로우 취소)
    @Transactional
    public void unfollow(FollowRequest followRequest) {

        // 팔로우 관계를 찾기 위해 followingUserId와 followedUserId로 Follow 엔티티를 조회
        Follow follow = followRepository.findByFollowingUserUserIdAndFollowedUserUserId(
                followRequest.getFollowingId(),
                findUserByNickname(followRequest.getFollowedUserNickname()).getUserId())
            .orElseThrow(FollowNotFound::new);

        // 팔로우 관계 삭제
        followRepository.delete(follow);
    }

    // accessToken 토큰으로 사용자 찾기
    @Transactional
    public User findByAccessToken(HttpServletRequest httpServletRequest) {
        log.info("findByAccessToken 실행");
        String accessToken = null;

        // accessToken 값 추출
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
            }
        }
        // accessToken 으로 UserId 추출
        Long userId = null;
        userId = jwtTokenizer.getUserIdFromToken(accessToken);
        User user = findUserById(userId).orElseThrow(UserNotFound::new);
        return user;
    }

    //사용자 조회
    public Optional<User> findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user;
    }

    //닉네임으로 사용자 조회
    @Transactional
    public User findUserByNickname(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);

        return user.orElseThrow(UserNotFound::new);
    }

    // 팔로워 및 팔로잉 수 조회 메서드
    public FollowCountResponse getFollowCounts(String nickname) {

        User user = userRepository.findByNickname(nickname)
            .orElseThrow(UserNotFound::new);

        int followerCount = followRepository.countByFollowedUser(user);
        int followingCount = followRepository.countByFollowingUser(user);

        return new FollowCountResponse(followerCount, followingCount);
    }

    // 팔로워 목록 조회 메서드
    public List<FollowListResponse> findFollowersByUserId(Long userId, Long myUserId) {
        List<Follow> followers = followRepository.findByFollowedUserUserId(userId);
        List<FollowListResponse> followListResponses = new ArrayList<>();

        for (Follow follow : followers) {
            User followingUser = follow.getFollowingUser();
            String profileImageUrl = "https://via.placeholder.com/150";
            if (followingUser.getProfile().getProfileImages() != null
                && followingUser.getProfile().getProfileImages().getFilePath() != null) {
                profileImageUrl = followingUser.getProfile().getProfileImages().getFilePath();
            }

            String intro = "자기 소개 없음";
            if (followingUser.getProfile().getIntro() != null && !followingUser.getProfile()
                .getIntro().isEmpty()) {
                intro = followingUser.getProfile().getIntro();

                // intro가 15자를 넘으면 잘라내고 "..."을 붙여줌
                if (intro.length() > 15) {
                    intro = intro.substring(0, 15) + "...";
                }
            }

            FollowListResponse response = new FollowListResponse(
                myUserId,
                followingUser.getUserId(),
                followingUser.getNickname(),
                profileImageUrl, // 프로필 사진 URL
                intro // 한줄 소개
            );
            followListResponses.add(response);
        }

        return followListResponses;
    }

    // 팔로잉 목록 조회 메서드
    public List<FollowListResponse> findFollowingsByUserId(Long userId, Long myUserId) {
        List<Follow> followings = followRepository.findByFollowingUserUserId(userId);
        List<FollowListResponse> followListResponses = new ArrayList<>();

        for (Follow follow : followings) {
            User followedUser = follow.getFollowedUser();
            String profileImageUrl = "https://via.placeholder.com/150";
            if (followedUser.getProfile().getProfileImages() != null
                && followedUser.getProfile().getProfileImages().getFilePath() != null) {
                profileImageUrl = followedUser.getProfile().getProfileImages().getFilePath();
            }

            String intro = "자기 소개 없음";
            if (followedUser.getProfile().getIntro() != null && !followedUser.getProfile()
                .getIntro().isEmpty()) {
                intro = followedUser.getProfile().getIntro();

                if (intro.length() > 15) {
                    intro = intro.substring(0, 15) + "...";
                }
            }

            FollowListResponse response = new FollowListResponse(
                myUserId,
                followedUser.getUserId(),
                followedUser.getNickname(),
                profileImageUrl, // 프로필 사진 URL
                intro // 한줄 소개
            );
            followListResponses.add(response);
        }

        return followListResponses;
    }
}
