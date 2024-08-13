package com.fluffytime.userpage.service;

import com.fluffytime.domain.Profile;
import com.fluffytime.domain.ProfileImages;
import com.fluffytime.domain.User;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import com.fluffytime.mypage.request.PostDto;
import com.fluffytime.post.aws.S3Service;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.ProfileImagesRepository;
import com.fluffytime.repository.ProfileRepository;
import com.fluffytime.repository.UserRepository;
import com.fluffytime.userpage.exception.NotFoundUserPage;
import com.fluffytime.userpage.response.UserPageInformationDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
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
    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final ProfileImagesRepository profileImagesRepository;
    private final JwtTokenizer jwtTokenizer;
    private final S3Service s3Service;

    // 사용자 조회(userId로 조회)
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long userId) {
        log.info("findUserById 실행");
        return userRepository.findById(userId);
    }

    // 사용자 조회(nickname으로 조회)
    @Transactional(readOnly = true)
    public User findUserByNickname(String nickname) {
        log.info("findUserByNickname 실행");
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        return optionalUser.orElse(null);
    }

    // accessToken 토큰으로 사용자 찾기
    @Transactional(readOnly = true)
    public User findByAccessToken(HttpServletRequest httpServletRequest) {
        log.info("findByAccessToken 실행");
        // accessTokne 값 추출
        String accessToken = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
            }
        }
        log.info(accessToken);
        // accessToken값으로  UserId 추출
        Long userId = Long.valueOf(
            ((Integer) jwtTokenizer.parseAccessToken(accessToken).get("userId")));
        // id(pk)에 해당되는 사용자 추출
        return findUserById(userId).get();
    }

    // 접근한 사용자와 실제 권한을 가진 사용자가 동일한지 판단하는 메서드
    public boolean isUserAuthorized(String accessNickname, String actuallyNickname) {
        return accessNickname.equals(actuallyNickname);
    }

    // 유저 페이지 정보 불러오기 응답 dto 구성
    @Transactional(readOnly = true)
    public UserPageInformationDto createUserPageInformationDto(String nickname) {
        User user = findUserByNickname(nickname);
        if (user != null) {
            log.info("createMyPageResponseDto 실행 >> 해당 유저가 존재하여 UserPageInformationDto를 구성");
            String nickName = user.getNickname(); // 닉네임

            // 기존 게시물 리스트에서 필요한 데이터만(이미지) 담은 postDto 리스트로 변환
            List<PostDto> postsList = user.getPostList().stream()
                // 한 포스트에 쓰인 사진 리스트 중 첫번째 사진을 썸네일로 설정하여 해당 파일의 경로 사용
                .map(post -> {
                    String thumbnailPath = post.getPostImages().isEmpty() ?
                        null : // 이미지가 없으면 null 사용
                        post.getPostImages().get(0).getFilepath();

                    return new PostDto(post.getPostId(), thumbnailPath);
                })
                .collect(Collectors.toList());

            // 게시물 리스트가 비어있을때
            if (postsList.isEmpty()) {
                postsList = null;
            }

            UserPageInformationDto userPageInformationDto = UserPageInformationDto.builder()
                .nickname(nickName)
                .postsList(postsList)
                .build();

            Profile profile = user.getProfile(); //프로필 객체
            if (profile == null) {
                return userPageInformationDto;
            } else {
                ProfileImages profileImages = profile.getProfileImages(); // 프로필 이미지 객체
                String fileUrl; // 프로필 이미지 업로드 URL
                if (profileImages == null) {
                    log.info("createProfileResponseDto 실행 >> 해당 유저는 프로필 미등록 상태");
                    fileUrl = null;
                } else {
                    log.info("createProfileResponseDto 실행 >> 해당 유저는 프로필 등록 상태");
                    fileUrl = profileImages.getFilePath();
                }

                userPageInformationDto.setPetName(profile.getPetName()); // 반려동물 이름
                userPageInformationDto.setPetSex(profile.getPetSex()); // 반려동물 성별
                userPageInformationDto.setPetAge(profile.getPetAge()); // 반려동물 나이
                userPageInformationDto.setIntro(profile.getIntro()); //소개글
                userPageInformationDto.setFileUrl(fileUrl);
                return userPageInformationDto;
            }
        } else {
            log.info("createMyPageResponseDto 실행 >> 해당 유저가 존재하지 않아 NOT_FOUND_MYPAGE 예외 발생");
            throw new NotFoundUserPage();
        }

    }
}
