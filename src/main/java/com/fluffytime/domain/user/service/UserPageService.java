package com.fluffytime.domain.user.service;

import com.fluffytime.domain.board.entity.enums.TempStatus;
import com.fluffytime.domain.user.dao.UserBlockDao;
import com.fluffytime.domain.user.dto.response.BlockUserListResponse;
import com.fluffytime.domain.user.dto.response.PostResponse;
import com.fluffytime.domain.user.dto.response.UserBlockResponse;
import com.fluffytime.domain.user.dto.response.UserPageInformationResponse;
import com.fluffytime.domain.user.entity.Profile;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.exception.UserPageNotFound;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.global.auth.jwt.exception.TokenNotFound;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPageService {

    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;
    private final MyPageService myPageService;
    private final UserBlockDao userBlockDao;


    // 기존 게시물 리스트에서 필요한 데이터만(이미지) 담은 postDto 리스트로 변환하는 메서드
    @Transactional
    public List<PostResponse> postList(User user) {
        return user.getPostList().stream()
            // TempStatus가 TEMP가 아닌것만 필터링(임시저장글 제외)
            .filter(post -> post.getTempStatus() != TempStatus.TEMP)
            // 한 포스트에 쓰인 사진 리스트 중 첫번째 사진을 썸네일로 설정하여 해당 파일의 경로 사용
            .map(post -> {
                String filePath = post.getPostImages().isEmpty() ? null // 이미지가 없을 경우 null 저장
                    : post.getPostImages().getFirst().getFilepath();
                String mineType = post.getPostImages().isEmpty() ? null // 이미지가 없을 경우 null 저장
                    : post.getPostImages().getFirst().getMimetype();
                return new PostResponse(post.getPostId(), filePath, mineType);
            })
            .collect(Collectors.collectingAndThen(Collectors.toList(), list -> { // 역순
                Collections.reverse(list);
                return list;
            }));
    }

    // UserPageInformationDto 생성 메서드
    public UserPageInformationResponse createResponseDto(User user, List<PostResponse> postsList,
        Profile profile, boolean isUserBlocked) {
        return UserPageInformationResponse.builder()
            .nickname(user.getNickname()) // 닉네임
            .postsList(postsList) // 유저의 게시물 리스트
            .petName(profile.getPetName()) // 반려동물 이름
            .petSex(profile.getPetSex()) // 반려동물 성별
            .petAge(profile.getPetAge()) // 반려동물 나이
            .intro(profile.getIntro()) // 소개글
            .fileUrl(myPageService.profileFileUrl(profile.getProfileImages())) // 프로필 파일 경로
            .publicStatus(profile.getPublicStatus()) // 프로필 공개 여부
            .isUserBlocked(isUserBlocked) // 해당 유저를 사용자가 차단 했는지 여부
            .build();
    }

    // 유저 페이지 정보 불러오기  메서드
    @Transactional
    public UserPageInformationResponse createUserPageInformationDto(String nickname,
        HttpServletRequest httpServletRequest) {
        User me = myPageService.findByAccessToken(httpServletRequest);
        User user = myPageService.findUserByNickname(nickname);

        if (me == null) {
            throw new TokenNotFound();
        }

        if (user != null) {
            log.info("UserPageInformationDto 실행 >> 해당 유저가 존재하여 UserPageInformationDto를 구성");
            Profile profile = user.getProfile(); //프로필 객체
            boolean isUserBlocked = isUserBlocked(me.getNickname(), user.getNickname());

            // 기존 게시물 리스트에서 필요한 데이터만(이미지) 담은 postDto 리스트로 변환
            List<PostResponse> postsList = postList(user);

            // 게시물 리스트가 비어있을때
            if (postsList.isEmpty()) {
                postsList = null;
            }

            return createResponseDto(user, postsList, profile, isUserBlocked);

        } else {
            log.info("UserPageInformationDto 실행 >> 해당 유저가 존재하지 않아 NOT_FOUND_USERPAGE 예외 발생");
            throw new UserPageNotFound();
        }

    }

    // 유저 차단  메서드
    @Transactional
    public UserBlockResponse userBlock(String targetUser,
        HttpServletRequest httpServletRequest) {
        log.info("userBlock 실행");
        String blocker = myPageService.findByAccessToken(httpServletRequest).getNickname();
        UserBlockResponse userBlockResponse = new UserBlockResponse(true);
        if (blocker == null) {
            userBlockResponse.setUserBlockResult(false);
            throw new TokenNotFound();
        }
        userBlockDao.saveUserBlockList(blocker, targetUser);
        return userBlockResponse;
    }

    // 유저 차단 여부  메서드
    @Transactional
    public boolean isUserBlocked(String blocker, String targetUser) {
        log.info("isUserBlocked 실행");
        Set<String> blockedUsers = userBlockDao.getUserBlockList(blocker);
        return blockedUsers.contains(targetUser);
    }

    // 유저 차단 해제  메서드
    @Transactional
    public UserBlockResponse removeUserBlock(String targetUser,
        HttpServletRequest httpServletRequest) {
        log.info("removeUserBlock 실행");
        String blocker = myPageService.findByAccessToken(httpServletRequest).getNickname();
        UserBlockResponse userBlockResponse = new UserBlockResponse(true);

        if (blocker == null) {
            userBlockResponse.setUserBlockResult(false);
            throw new TokenNotFound();
        }

        userBlockDao.removeUserBlockList(blocker, targetUser);

        return userBlockResponse;
    }

    // 유저 차단 목록 생성  메서드
    public List<Map<String, String>> BlockList(Set<String> blockedUsers) {
        // 사용자명 - 프로필 사진 url를 담을 리스트
        List<Map<String, String>> userBlockList = new ArrayList<>();
        // 리스트에 목록 추가하기
        for (String nickname : blockedUsers) {
            // 프로필 객체 찾기
            Profile profile = myPageService.findUserByNickname(nickname).getProfile();
            // 프로필 사진 url 찾기
            String fileUrl = myPageService.profileFileUrl(profile.getProfileImages());
            // 닉네임과 URL을 매칭하여 맵에 저장
            Map<String, String> blockUserMap = new HashMap<>();
            blockUserMap.put("nickname", nickname);
            blockUserMap.put("fileUrl", fileUrl);
            // 리스트에 추가
            userBlockList.add(blockUserMap);
        }
        return userBlockList;
    }

    // 유저 차단 목록 가져오기  메서드
    @Transactional
    public BlockUserListResponse blockUserList(HttpServletRequest httpServletRequest) {
        log.info("blockUserList 실행");
        String blocker = myPageService.findByAccessToken(httpServletRequest).getNickname();
        if (blocker == null) {
            throw new TokenNotFound();
        }
        // 차단된 사용자 리스트 가져오기
        Set<String> blockedUsers = userBlockDao.getUserBlockList(blocker);
        return new BlockUserListResponse(BlockList(blockedUsers));
    }
}
