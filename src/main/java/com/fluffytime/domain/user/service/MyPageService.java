package com.fluffytime.domain.user.service;

import com.fluffytime.domain.board.entity.Bookmark;
import com.fluffytime.domain.board.entity.enums.TempStatus;
import com.fluffytime.domain.board.repository.BookmarkRepository;
import com.fluffytime.domain.notification.repository.AdminNotificationRepository;
import com.fluffytime.domain.notification.service.AdminNotificationService;
import com.fluffytime.domain.user.dto.request.ProfileRequest;
import com.fluffytime.domain.user.dto.response.CheckUsernameResponse;
import com.fluffytime.domain.user.dto.response.ImageResultResponse;
import com.fluffytime.domain.user.dto.response.MyPageInformationResponse;
import com.fluffytime.domain.user.dto.response.PostResponse;
import com.fluffytime.domain.user.dto.response.ProfileInformationResponse;
import com.fluffytime.domain.user.dto.response.RequestResultResponse;
import com.fluffytime.domain.user.entity.Profile;
import com.fluffytime.domain.user.entity.ProfileImages;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.exception.MyPageNotFound;
import com.fluffytime.domain.user.exception.NoProfileImage;
import com.fluffytime.domain.user.repository.ProfileRepository;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.global.auth.jwt.exception.TokenNotFound;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.global.common.exception.global.UserNotFound;
import com.fluffytime.global.config.aws.S3Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {

    private final AdminNotificationService adminNotificationService;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final BookmarkRepository bookmarkRepository;
    private final AdminNotificationRepository adminNotificationRepository;
    private final JwtTokenizer jwtTokenizer;
    private final S3Service s3Service;


    // 사용자 조회(userId로 조회)  메서드
    @Transactional
    public User findUserById(Long userId) {
        log.info("findUserById 실행");
        return userRepository.findById(userId).orElse(null);
    }

    // 사용자 조회(nickname으로 조회)  메서드
    @Transactional
    public User findUserByNickname(String nickname) {
        log.info("findUserByNickname 실행");
        return userRepository.findByNickname(nickname).orElse(null);
    }

    // accessToken 토큰으로 사용자 찾기  메서드
    @Transactional
    public User findByAccessToken(HttpServletRequest httpServletRequest) {
        log.info("findByAccessToken 실행");
        String accessToken = jwtTokenizer.getTokenFromCookie(httpServletRequest, "accessToken");
        if (accessToken == null) {
            throw new TokenNotFound();
        }
        // accessToken값으로  UserId 추출
        Long userId = Long.valueOf(
            ((Integer) jwtTokenizer.parseAccessToken(accessToken).get("userId")));
        // id(pk)에 해당되는 사용자 추출
        return findUserById(userId);
    }

    // 접근한 사용자와 실제 권한을 가진 사용자가 동일한지 판단하는 메서드
    @Transactional
    public boolean isUserAuthorized(String accessNickname, String actuallyNickname) {
        return accessNickname.equals(actuallyNickname);
    }

    // 프로필 사진 url 조회  메서드
    @Transactional
    public String profileFileUrl(ProfileImages profileImages) {
        if (profileImages == null) {
            log.info("createProfileResponseDto 실행 >> 프로필 사진 미등록 상태");
            return null;
        } else {
            log.info("createProfileResponseDto 실행 >> 프로필 사진 등록 상태");
            return profileImages.getFilePath();
        }
    }

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

    // 북마크 게시물 리스트  메서드
    @Transactional
    public List<PostResponse> bookmarkList(List<Bookmark> bookmarks) {
        return bookmarks.stream()
            .map(Bookmark::getPost) // 북마크에서 게시글을 가져옴
            .map(post -> {
                // 첫 번째 이미지의 파일 경로를 가져옴
                String filePath = post.getPostImages().isEmpty() ? null
                    : post.getPostImages().get(0).getFilepath();
                String mineType = post.getPostImages().isEmpty() ? null // 이미지가 없을 경우 null 저장
                    : post.getPostImages().getFirst().getMimetype();
                return new PostResponse(post.getPostId(), filePath, mineType);
            })
            .collect(Collectors.toList());
    }

    // MyPageInformationDto 생성 메서드
    @Transactional
    public MyPageInformationResponse createResponseDto(String nickname,
        List<PostResponse> postsList,
        List<PostResponse> bookmarkList, Profile profile) {
        return MyPageInformationResponse.builder()
            .nickname(nickname) // 닉네임
            .postsList(postsList) // 유저의 게시물 리스트
            .bookmarkList(bookmarkList) // 북마크 리스트
            .petName(profile.getPetName()) // 반려동물 이름
            .petSex(profile.getPetSex()) // 반려동물 성별
            .petAge(profile.getPetAge()) // 반려동물 나이
            .intro(profile.getIntro()) // 소개글
            .fileUrl(profileFileUrl(profile.getProfileImages())) // 프로필 파일 경로
            .build();
    }

    // 마이페이지 정보 불러오기 응답 dto 구성
    @Transactional
    public MyPageInformationResponse createMyPageResponseDto(String nickname) {
        User user = findUserByNickname(nickname);

        if (user != null) {
            log.info("createMyPageResponseDto 실행 >> 해당 유저가 존재하여 MyPageInformationDto를 구성");
            // 기존 게시물 리스트에서 필요한 데이터만(이미지) 담은 postDto 리스트로 변환
            List<PostResponse> postsList = postList(user);

            // 해당 유저의 게시물이 없을때
            if (postsList.isEmpty()) {
                postsList = null;
            }

            // 북마크 게시물 리스트
            List<Bookmark> bookmarks = bookmarkRepository.findByUserUserId(user.getUserId());
            List<PostResponse> bookmarkList = bookmarkList(bookmarks);

            // 기능 구현후 팔로우, 팔로워 수 추가 예정
            return createResponseDto(user.getNickname(), postsList, bookmarkList,
                user.getProfile());

        } else {
            log.info("createMyPageResponseDto 실행 >> 해당 유저가 존재하지 않아 NOT_FOUND_MYPAGE 예외 발생");
            throw new MyPageNotFound();
        }

    }

    // ProfileInformationDto 생성 메서드
    @Transactional
    public ProfileInformationResponse createResponseDto(String nickname, String email,
        Profile profile) {
        return ProfileInformationResponse.builder()
            .nickname(nickname) // 닉네임
            .email(email) // 이메일
            .intro(profile.getIntro()) //  소개글
            .petName(profile.getPetName()) // 반려동물 이름
            .petSex(profile.getPetSex()) // 반려동물 성별
            .petAge(profile.getPetAge()) // 반려동물 나이
            .petCategory(profile.getPetCategory()) // 반려동물 카테고리
            .publicStatus(profile.getPublicStatus()) // 계정 공개 여부
            .fileUrl(profileFileUrl(profile.getProfileImages())) // 프로필 파일 경로
            .build();

    }

    // 프로필 정보 불러오기 메서드
    @Transactional
    public ProfileInformationResponse createProfileResponseDto(String nickname) {
        log.info("createProfileResponseDto 실행");
        User user = findUserByNickname(nickname);

        if (user != null) {
            log.info("createProfileResponseDto 실행 >> 해당 유저가 존재하여 ProfileInformationDto 구성");
            return createResponseDto(nickname, user.getEmail(), user.getProfile());

        } else {
            log.info("createProfileResponseDto 실행 >> 해당 유저가 존재하지 않아  NOT_FOUND_USER 예외 발생");
            throw new UserNotFound();
        }
    }

    // 닉네임 중복 여부 검증 메서드
    @Transactional
    public CheckUsernameResponse nicknameExists(String nickname) {
        log.info("nicknameExists 실행 >> CheckUsernameDto 구성");
        return CheckUsernameResponse.builder()
            .result(userRepository.existsByNickname(nickname))
            .build();
    }


    // 프로필 업데이트 메서드
    @Transactional
    public RequestResultResponse profileSave(ProfileRequest profileRequest) {
        User user = findUserByNickname(profileRequest.getNickname());

        if (user != null) {
            log.info("profileSave 실행 >> 해당 유저가 존재하여 프로필을 업데이트하고 UpdateResultDto 구성");
            user.setNickname(profileRequest.getUsername());

            Profile profile = user.getProfile();
            profile.setIntro(profileRequest.getIntro());
            profile.setPetName(profileRequest.getPetName());
            profile.setPetSex(profileRequest.getPetSex());
            profile.setPetAge(Long.parseLong(profileRequest.getPetAge()));
            profile.setPetCategory(profileRequest.getPetCategory());
            profile.setPublicStatus(profileRequest.getPublicStatus());

            userRepository.save(user);
            profileRepository.save(profile);

            return RequestResultResponse.builder()
                .result(true)
                .build();
        } else {
            log.info("profileSave 실행 >> 해당 유저가 존재하지 않아 프로필 수정 불가 발생");
            return RequestResultResponse.builder()
                .result(false)
                .build();
        }
    }

    // ProfileImages 생성 메서드
    public ProfileImages createResponseDto(MultipartFile file, String s3iFileName, String Url) {
        return ProfileImages.builder()
            .fileName(file.getOriginalFilename()) // 원본 파일 이름
            .s3iFileName(s3iFileName)
            .filePath(Url) // S3에 저장된 파일 URL 또는 경로
            .fileSize(file.getSize()) // 파일 크기
            .mimeType(file.getContentType()) // MIME 타입
            .build();
    }

    // 프로필 이미지 업데이트 메서드
    @Transactional
    public ImageResultResponse uploadProfileImage(String nickname,
        MultipartFile file) {
        log.info("uploadProfileImage 실행 ");
        User user = findUserByNickname(nickname);
        Profile profile = user.getProfile();

        ImageResultResponse imageResultResponse = new ImageResultResponse();
        imageResultResponse.setResult(true);

        if (file == null || file.isEmpty()) {
            imageResultResponse.setResult(false);
            throw new NoProfileImage(); // 파일이 없거나 비어있는 경우 예외 처리
        }

        String s3iFileName = s3Service.uploadFile(file); // 파일 업로드
        String Url = s3Service.getFileUrl(s3iFileName);

        ProfileImages profileImage = createResponseDto(file, s3iFileName, Url);

        profile.setProfileImages(profileImage);
        profileRepository.save(profile);
        imageResultResponse.setFileUrl(Url);

        return imageResultResponse;
    }

    // 이미지 삭제하기 메서드
    @Transactional
    public ImageResultResponse deleteProfileImage(String nickname) {
        log.info("uploadProfileImage 실행");
        User user = findUserByNickname(nickname);
        ImageResultResponse imageResultResponse = new ImageResultResponse();
        imageResultResponse.setResult(true);

        if (user == null) {
            log.info("uploadProfileImage 실행 >> 해당 유저가 없습니다. ");
            imageResultResponse.setResult(false);
            throw new UserNotFound();
        }

        Profile profile = user.getProfile();
        if (profile.getProfileImages() == null) {
            log.info("uploadProfileImage 실행 >> 등록된 프로필 사진이 없습니다. ");
            throw new NoProfileImage();
        }

        imageResultResponse.setFileUrl(profile.getProfileImages().getFilePath());
        profile.setProfileImages(null);
        profileRepository.save(profile);

        return imageResultResponse;
    }


    // 쿠기 삭제 메서드
    @Transactional
    public void deleteCookie(HttpServletResponse response) {
        // accessToken 쿠키 삭제
        Cookie aceessTokenCookie = new Cookie("accessToken", null);
        aceessTokenCookie.setPath("/");
        aceessTokenCookie.setHttpOnly(true);
        aceessTokenCookie.setMaxAge(0);
        response.addCookie(aceessTokenCookie);

        // refreshToken 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);
    }

    // 회원 탈퇴 기능 메서드
    @Transactional
    public RequestResultResponse AccountDelete(String nickname, HttpServletResponse response) {
        User user = findUserByNickname(nickname);

        if (user != null) { // 유저가 있다면 계정 삭제 진행
            adminNotificationService.withdrawJoinNotification(user);
            log.info("AccountDelete 실행 >> 해당 유저가 존재하여 회원 탈퇴");
            userRepository.delete(user);
            // 쿠기 삭제
            deleteCookie(response);
            return RequestResultResponse.builder()
                .result(true)
                .build();
        } else {
            log.info("AccountDelete 실행 >> 해당 유저가 존재하지 않아 NOT_FOUND_USER 예외 발생");
            return RequestResultResponse.builder()
                .result(false)
                .build();

        }
    }
}
