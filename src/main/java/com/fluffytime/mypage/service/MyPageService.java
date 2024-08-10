package com.fluffytime.mypage.service;

import com.fluffytime.common.exception.global.NotFoundProfile;
import com.fluffytime.common.exception.global.NotFoundUser;
import com.fluffytime.domain.Profile;
import com.fluffytime.domain.ProfileImages;
import com.fluffytime.domain.User;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import com.fluffytime.mypage.exception.NoProfileImage;
import com.fluffytime.mypage.exception.NotFoundMyPage;
import com.fluffytime.mypage.request.PostDto;
import com.fluffytime.mypage.request.ProfileDto;
import com.fluffytime.mypage.response.CheckUsernameDto;
import com.fluffytime.mypage.response.ImageResultDto;
import com.fluffytime.mypage.response.MyPageInformationDto;
import com.fluffytime.mypage.response.ProfileInformationDto;
import com.fluffytime.mypage.response.RequestResultDto;
import com.fluffytime.post.aws.S3Service;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.ProfileImagesRepository;
import com.fluffytime.repository.ProfileRepository;
import com.fluffytime.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
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

    // 마이페이지 정보 불러오기 응답 dto 구성
    @Transactional(readOnly = true)
    public MyPageInformationDto createMyPageResponseDto(String nickname) {
        User user = findUserByNickname(nickname);
        if (user != null) {
            log.info("createMyPageResponseDto 실행 >> 해당 유저가 존재하여 MyPageInformationDto를 구성");
            String nickName = user.getNickname(); // 닉네임

            // 기존 게시물 리스트에서 필요한 데이터만 담은 postDto 리스트로 변환
            List<PostDto> postsList = user.getPostList().stream()
                .map(post -> new PostDto(post.getContent()))
                .collect(Collectors.toList());

            // 게시물 리스트가 비어있을때
            if (postsList.isEmpty()) {
                postsList = null;
            }

            MyPageInformationDto myPageInformationDto = MyPageInformationDto.builder()
                .nickname(nickName)
                .postsList(postsList)
                .build();

            Profile profile = user.getProfile(); //프로필 객체
            if (profile == null) {
                return myPageInformationDto;
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

                myPageInformationDto.setPetName(profile.getPetName()); // 반려동물 이름
                myPageInformationDto.setPetSex(profile.getPetSex()); // 반려동물 성별
                myPageInformationDto.setPetAge(profile.getPetAge()); // 반려동물 나이
                myPageInformationDto.setIntro(profile.getIntro()); //소개글
                myPageInformationDto.setFileUrl(fileUrl);
                return myPageInformationDto;
            }
        } else {
            log.info("createMyPageResponseDto 실행 >> 해당 유저가 존재하지 않아 NOT_FOUND_MYPAGE 예외 발생");
            throw new NotFoundMyPage();
        }

    }

    // 프로필 정보 불러오기 응답 dto구성
    @Transactional
    public ProfileInformationDto createProfileResponseDto(String nickname) {
        log.info("createProfileResponseDto 실행");
        User user = findUserByNickname(nickname);

        if (user != null) {
            log.info("createProfileResponseDto 실행 >> 해당 유저가 존재하여 ProfileInformationDto 구성");
            String email = user.getEmail(); // 이메일

            Profile profile = user.getProfile(); // 프로필 객체
            ProfileImages profileImages = profile.getProfileImages(); // 프로필 이미지 객체
            // 사용자는 있으나, 프로필이 없는 경우 기본 뼈대 프로필 생성 (회원가입 후 프로필을 수정하지 않은 경우에 해당)
            if (profile == null) {
                throw new NotFoundProfile();
            }
            String petName = profile.getPetName(); // 반려동물 이름
            String petSex = profile.getPetSex(); // 반려동물 성별
            Long petAge = profile.getPetAge(); // 반려동물 나이
            String intro = profile.getIntro(); //소개글
            String category = profile.getPetCategory(); // 카테고리
            String publicStatus = profile.getPublicStatus(); // 계정 비공개/공개 여부
            String fileUrl; // 프로필 이미지 업로드 URL
            if (profileImages == null) {
                log.info("createProfileResponseDto 실행 >> 해당 유저는 프로필 미등록 상태");
                fileUrl = null;
            } else {
                log.info("createProfileResponseDto 실행 >> 해당 유저는 프로필 등록 상태");
                fileUrl = profileImages.getFilePath();
            }
            return ProfileInformationDto.builder()
                .nickname(nickname)
                .email(email)
                .intro(intro)
                .petName(petName)
                .petSex(petSex)
                .petAge(petAge)
                .petCategory(category)
                .publicStatus(publicStatus)
                .fileUrl(fileUrl)
                .build();

        } else {
            log.info("createProfileResponseDto 실행 >> 해당 유저가 존재하지 않아  NOT_FOUND_USER 예외 발생");
            throw new NotFoundUser();
        }
    }

    // 닉네임 중복 응답 dto 구성
    @Transactional
    public CheckUsernameDto nicknameExists(String nickname) {
        log.info("nicknameExists 실행 >> CheckUsernameDto 구성");
        return CheckUsernameDto.builder()
            .result(userRepository.existsByNickname(nickname))
            .build();
    }

    // 프로필 등록(기본틀)
    @Transactional
    public RequestResultDto createProfile(String nickname) {
        Profile basicProfile = new Profile("none", Long.valueOf(0), "none");
        RequestResultDto requestResultDto = new RequestResultDto();
        User user = findUserByNickname(nickname);

        if (user != null) { // 사용자가 있을때 프로필이 없다면 프로필 생성
            log.info("createProfile 실행 >> 해당 유저가 존재하여 프로필을 등록 하고 updateResultDto 구성");
            user.setProfile(basicProfile);
            userRepository.save(user);
            requestResultDto.setResult(true);
        } else { // 유저가 없으므로 프로필 생성 실패
            log.info("createProfile 실행 >> 해당 유저가 존재하지 않아 프로필 생성 실패");
            requestResultDto.setResult(false);
        }
        return requestResultDto;
    }

    // 프로필 수정
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

    // 회원 탈퇴 기능
    @Transactional
    public RequestResultDto AccountDelete(String nickname) {
        User user = findUserByNickname(nickname);
        if (user != null) { // 유저가 있다면 계정 삭제 진행
            log.info("AccountDelete 실행 >> 해당 유저가 존재하여 회원 탈퇴");
            userRepository.delete(user);
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

    // 프로필 이미지 등록 / 수정
    public ImageResultDto uploadProfileImage(String nickname,
        MultipartFile file) {
        log.info("uploadProfileImage 실행 ");
        ImageResultDto imageResultDto = new ImageResultDto();
        imageResultDto.setResult(true);
        if (file == null || file.isEmpty()) {
            imageResultDto.setResult(false);
            throw new NoProfileImage(); // 파일이 없거나 비어있는 경우 예외 처리
        }
        String s3iFileName = s3Service.uploadFile(file); // 파일 업로드
        String Url = s3Service.getFileUrl(s3iFileName);
        ProfileImages profileImage = ProfileImages.builder()
            .fileName(file.getOriginalFilename()) // 원본 파일 이름
            .s3iFileName(s3iFileName)
            .filePath(Url) // S3에 저장된 파일 URL 또는 경로
            .fileSize(file.getSize()) // 파일 크기
            .mimeType(file.getContentType()) // MIME 타입
            .build();
        User user = findUserByNickname(nickname);
        Profile profile = user.getProfile();
        profile.setProfileImages(profileImage);
        profileRepository.save(profile);
        imageResultDto.setFileUrl(Url);
        return imageResultDto;
    }


    // 이미지 삭제하기
    public ImageResultDto deleteProfileImage(String nickname) {
        log.info("uploadProfileImage 실행");
        User user = findUserByNickname(nickname);
        String ProfileImageId;
        ImageResultDto imageResultDto = new ImageResultDto();
        imageResultDto.setResult(true);
        if (user == null) {
            log.info("uploadProfileImage 실행 >> 해당 유저가 없습니다. ");
            imageResultDto.setResult(false);
            throw new NotFoundUser();
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
}
