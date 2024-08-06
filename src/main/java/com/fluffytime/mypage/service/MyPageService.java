package com.fluffytime.mypage.service;

import com.fluffytime.domain.Profile;
import com.fluffytime.domain.User;
import com.fluffytime.mypage.exception.MyPageException;
import com.fluffytime.mypage.exception.MyPageExceptionCode;
import com.fluffytime.mypage.request.PostDto;
import com.fluffytime.mypage.request.ProfileDto;
import com.fluffytime.mypage.response.CheckUsernameDto;
import com.fluffytime.mypage.response.MyPageInformationDto;
import com.fluffytime.mypage.response.ProfileInformationDto;
import com.fluffytime.mypage.response.RequestResultDto;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.ProfileRepository;
import com.fluffytime.repository.UserRepository;
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
public class MyPageService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;

    // 마이페이지 사용자 조회(userId로 조회)
    @Transactional(readOnly = true)
    public Optional<User> findUserById(String userId) {
        log.info("findUserById 실행");
        Long username = Long.parseLong(userId); // String -> Long 변환
        return userRepository.findById(username);
    }

    // 마이페이지 사용자 조회(nickname으로 조회)
    @Transactional(readOnly = true)
    public User findUserByNickname(String nickname) {
        log.info("findUserByNickname 실행");
        return userRepository.findByNickname(nickname).orElseThrow(
            () -> new MyPageException(MyPageExceptionCode.NOT_FOUND_USER.getCode(),
                MyPageExceptionCode.NOT_FOUND_USER.getMessage()));
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
                .map(post -> new PostDto(post.getTitle()))
                .collect(Collectors.toList());

            Profile profile = user.getProfile(); //프로필 객체
            String petName = profile.getPetName(); // 반려동물 이름
            String petSex = profile.getPetSex(); // 반려동물 성별
            Long petAge = profile.getPetAge(); // 반려동물 나이
            String intro = profile.getIntro(); //소개글

            return MyPageInformationDto.builder()
                .code(MyPageExceptionCode.OK.getCode())
                .message(MyPageExceptionCode.OK.getMessage())
                .nickname(nickName)
                .postsList(postsList)
                .petName(petName)
                .petSex(petSex)
                .petAge(petAge)
                .intro(intro)
                .build();
        } else {
            log.info("createMyPageResponseDto 실행 >> 해당 유저가 존재하지 않아 NOT_FOUND_MYPAGE 예외 발생");
            throw new MyPageException(MyPageExceptionCode.NOT_FOUND_MYPAGE.getCode(),
                MyPageExceptionCode.NOT_FOUND_MYPAGE.getMessage());
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
            // 사용자는 있으나, 프로필이 없는 경우 기본 뼈대 프로필 생성 (회원가입 후 프로필을 수정하지 않은 경우에 해당)
            if (profile == null) {
                throw new MyPageException(MyPageExceptionCode.NOT_FOUND_PROFILE.getCode(),
                    MyPageExceptionCode.NOT_FOUND_PROFILE.getMessage());
            }
            String petName = profile.getPetName(); // 반려동물 이름
            String petSex = profile.getPetSex(); // 반려동물 성별
            Long petAge = profile.getPetAge(); // 반려동물 나이
            String intro = profile.getIntro(); //소개글
            String category = profile.getPetCategory(); // 카테고리
            String publicStatus = profile.getPublicStatus(); // 계정 비공개/공개 여부

            return ProfileInformationDto.builder()
                .code(MyPageExceptionCode.OK.getCode())
                .message(MyPageExceptionCode.OK.getCode())
                .nickname(nickname)
                .email(email)
                .intro(intro)
                .petName(petName)
                .petSex(petSex)
                .petAge(petAge)
                .petCategory(category)
                .publicStatus(publicStatus)
                .build();

        } else {
            log.info("createProfileResponseDto 실행 >> 해당 유저가 존재하지 않아  NOT_FOUND_USER 예외 발생");
            throw new MyPageException(MyPageExceptionCode.NOT_FOUND_USER.getCode(),
                MyPageExceptionCode.NOT_FOUND_USER.getMessage());
        }
    }

    // 닉네임 중복 응답 dto 구성
    @Transactional
    public CheckUsernameDto nicknameExists(String nickname) {
        log.info("nicknameExists 실행 >> CheckUsernameDto 구성");
        return CheckUsernameDto.builder()
            .code(MyPageExceptionCode.OK.getCode())
            .message(MyPageExceptionCode.OK.getMessage())
            .result(userRepository.existsByNickname(nickname))
            .build();
    }

    // 프로필 등록(기본틀)
    public RequestResultDto createProfile(String nickname) {
        Profile basicProfile = new Profile();
        RequestResultDto requestResultDto = new RequestResultDto();
        User user = findUserByNickname(nickname);

        if (user != null) { // 사용자가 있을때 프로필이 없다면 프로필 생성
            log.info("createProfile 실행 >> 해당 유저가 존재하여 프로필을 등록 하고 updateResultDto 구성");
            user.setProfile(basicProfile);
            userRepository.save(user);
            requestResultDto.setCode(MyPageExceptionCode.MYPAGE_CREATED.getCode());
            requestResultDto.setMessage(MyPageExceptionCode.MYPAGE_CREATED.getMessage());
            requestResultDto.setResult(true);
        } else { // 유저가 없으므로 프로필 생성 실패
            log.info("createProfile 실행 >> 해당 유저가 존재하지 않아 NOT_FOUND_USER 예외 발생");
            requestResultDto.setCode(MyPageExceptionCode.NOT_FOUND_USER.getCode());
            requestResultDto.setMessage(MyPageExceptionCode.NOT_FOUND_USER.getMessage());
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
                .code(MyPageExceptionCode.OK.getCode())
                .message(MyPageExceptionCode.OK.getMessage())
                .result(true)
                .build();
        } else {
            log.info("profileSave 실행 >> 해당 유저가 존재하지 않아 NOT_FOUND_PROFILE 예외 발생");
            return RequestResultDto.builder()
                .code(MyPageExceptionCode.NOT_FOUND_PROFILE.getCode())
                .message(MyPageExceptionCode.NOT_FOUND_PROFILE.getMessage())
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
                .code(MyPageExceptionCode.OK.getCode())
                .message(MyPageExceptionCode.OK.getMessage())
                .result(true)
                .build();
        } else {
            log.info("AccountDelete 실행 >> 해당 유저가 존재하지 않아 NOT_FOUND_USER 예외 발생");
            return RequestResultDto.builder()
                .code(MyPageExceptionCode.NOT_FOUND_PROFILE.getCode())
                .message(MyPageExceptionCode.NOT_FOUND_PROFILE.getMessage())
                .result(false)
                .build();

        }


    }
}
