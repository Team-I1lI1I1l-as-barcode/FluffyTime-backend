package com.fluffytime.mypage.service;

import com.fluffytime.auth.jwt.exception.TokenNotFound;
import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.common.exception.global.UserNotFound;
import com.fluffytime.config.aws.S3Service;
import com.fluffytime.domain.Bookmark;
import com.fluffytime.domain.Profile;
import com.fluffytime.domain.ProfileImages;
import com.fluffytime.domain.TempStatus;
import com.fluffytime.domain.User;
import com.fluffytime.mypage.exception.MyPageNotFound;
import com.fluffytime.mypage.exception.NoProfileImage;
import com.fluffytime.mypage.request.ProfileDto;
import com.fluffytime.mypage.response.CheckUsernameDto;
import com.fluffytime.mypage.response.ImageResultDto;
import com.fluffytime.mypage.response.MyPageInformationDto;
import com.fluffytime.mypage.response.PostDto;
import com.fluffytime.mypage.response.ProfileInformationDto;
import com.fluffytime.mypage.response.RequestResultDto;
import com.fluffytime.repository.BookmarkRepository;
import com.fluffytime.repository.ProfileRepository;
import com.fluffytime.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final BookmarkRepository bookmarkRepository;
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
    public List<PostDto> postList(User user) {
        return user.getPostList().stream()
            // TempStatus가 TEMP가 아닌것만 필터링(임시저장글 제외)
            .filter(post -> post.getTempStatus() != TempStatus.TEMP)
            // 한 포스트에 쓰인 사진 리스트 중 첫번째 사진을 썸네일로 설정하여 해당 파일의 경로 사용
            .map(post -> {
                String filePath = post.getPostImages().isEmpty() ? null // 이미지가 없을 경우 null 저장
                    : post.getPostImages().getFirst().getFilepath();
                return new PostDto(post.getPostId(), filePath);
            })
            .collect(Collectors.toList());
    }

    // 북마크 게시물 리스트  메서드
    @Transactional
    public List<PostDto> bookmarkList(List<Bookmark> bookmarks) {
        return bookmarks.stream()
            .map(Bookmark::getPost) // 북마크에서 게시글을 가져옴
            .map(post -> {
                // 첫 번째 이미지의 파일 경로를 가져옴
                String filePath = post.getPostImages().isEmpty() ? null
                    : post.getPostImages().get(0).getFilepath();
                // PostDto 생성
                return new PostDto(post.getPostId(), filePath);
            })
            .collect(Collectors.toList());
    }

    // MyPageInformationDto 생성 메서드
    @Transactional
    public MyPageInformationDto createResponseDto(String nickname, List<PostDto> postsList,
        List<PostDto> bookmarkList, Profile profile) {
        return MyPageInformationDto.builder()
            .nickname(nickname) // 닉네임
            .postsList(postsList) // 유저의 게시물 리스트
            .bookmarkList(bookmarkList) // 북마크 리스트
            .petName(profile.getPetName()) // 반려동물 이름
            .petSex(profile.getPetSex()) // 반려동물 성별
            .petAge(profile.getPetAge()) // 반려동물 나이
            .fileUrl(profileFileUrl(profile.getProfileImages())) // 프로필 파일 경로
            .build();
    }

    // 마이페이지 정보 불러오기 응답 dto 구성
    @Transactional
    public MyPageInformationDto createMyPageResponseDto(String nickname) {
        User user = findUserByNickname(nickname);

        if (user != null) {
            log.info("createMyPageResponseDto 실행 >> 해당 유저가 존재하여 MyPageInformationDto를 구성");
            // 기존 게시물 리스트에서 필요한 데이터만(이미지) 담은 postDto 리스트로 변환
            List<PostDto> postsList = postList(user);

            // 해당 유저의 게시물이 없을때
            if (postsList.isEmpty()) {
                postsList = null;
            }

            // 북마크 게시물 리스트
            List<Bookmark> bookmarks = bookmarkRepository.findByUserUserId(user.getUserId());
            List<PostDto> bookmarkList = bookmarkList(bookmarks);

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
    public ProfileInformationDto createResponseDto(String nickname, String email, Profile profile) {
        return ProfileInformationDto.builder()
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
    public ProfileInformationDto createProfileResponseDto(String nickname) {
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
    public CheckUsernameDto nicknameExists(String nickname) {
        log.info("nicknameExists 실행 >> CheckUsernameDto 구성");
        return CheckUsernameDto.builder()
            .result(userRepository.existsByNickname(nickname))
            .build();
    }


    // 프로필 업데이트 메서드
    @Transactional
    public RequestResultDto profileSave(ProfileDto profileDto) {
        User user = findUserByNickname(profileDto.getNickname());

        if (user != null) {
            log.info("profileSave 실행 >> 해당 유저가 존재하여 프로필을 업데이트하고 UpdateResultDto 구성");
            user.setNickname(profileDto.getUsername());

            Profile profile = user.getProfile();
            profile.setIntro(profileDto.getIntro());
            profile.setPetName(profileDto.getPetName());
            profile.setPetSex(profileDto.getPetSex());
            profile.setPetAge(Long.parseLong(profileDto.getPetAge()));
            profile.setPetCategory(profileDto.getPetCategory());
            profile.setPublicStatus(profileDto.getPublicStatus());

            userRepository.save(user);
            profileRepository.save(profile);

            return RequestResultDto.builder()
                .result(true)
                .build();
        } else {
            log.info("profileSave 실행 >> 해당 유저가 존재하지 않아 프로필 수정 불가 발생");
            return RequestResultDto.builder()
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
    public ImageResultDto uploadProfileImage(String nickname,
        MultipartFile file) {
        log.info("uploadProfileImage 실행 ");
        User user = findUserByNickname(nickname);
        Profile profile = user.getProfile();

        ImageResultDto imageResultDto = new ImageResultDto();
        imageResultDto.setResult(true);

        if (file == null || file.isEmpty()) {
            imageResultDto.setResult(false);
            throw new NoProfileImage(); // 파일이 없거나 비어있는 경우 예외 처리
        }

        String s3iFileName = s3Service.uploadFile(file); // 파일 업로드
        String Url = s3Service.getFileUrl(s3iFileName);

        ProfileImages profileImage = createResponseDto(file, s3iFileName, Url);

        profile.setProfileImages(profileImage);
        profileRepository.save(profile);
        imageResultDto.setFileUrl(Url);

        return imageResultDto;
    }

    // 이미지 삭제하기 메서드
    @Transactional
    public ImageResultDto deleteProfileImage(String nickname) {
        log.info("uploadProfileImage 실행");
        User user = findUserByNickname(nickname);
        ImageResultDto imageResultDto = new ImageResultDto();
        imageResultDto.setResult(true);

        if (user == null) {
            log.info("uploadProfileImage 실행 >> 해당 유저가 없습니다. ");
            imageResultDto.setResult(false);
            throw new UserNotFound();
        }

        Profile profile = user.getProfile();
        if (profile.getProfileImages() == null) {
            log.info("uploadProfileImage 실행 >> 등록된 프로필 사진이 없습니다. ");
            throw new NoProfileImage();
        }

        imageResultDto.setFileUrl(profile.getProfileImages().getFilePath());
        profile.setProfileImages(null);
        profileRepository.save(profile);

        return imageResultDto;
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
    public RequestResultDto AccountDelete(String nickname, HttpServletResponse response) {
        User user = findUserByNickname(nickname);

        if (user != null) { // 유저가 있다면 계정 삭제 진행
            log.info("AccountDelete 실행 >> 해당 유저가 존재하여 회원 탈퇴");
            userRepository.delete(user);
            // 쿠기 삭제
            deleteCookie(response);
            return RequestResultDto.builder()
                .result(true)
                .build();
        } else {
            log.info("AccountDelete 실행 >> 해당 유저가 존재하지 않아 NOT_FOUND_USER 예외 발생");
            return RequestResultDto.builder()
                .result(false)
                .build();

        }
    }
}
