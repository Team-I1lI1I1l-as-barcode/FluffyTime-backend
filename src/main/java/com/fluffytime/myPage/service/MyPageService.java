package com.fluffytime.myPage.service;

import com.fluffytime.domain.Profile;
import com.fluffytime.domain.User;
import com.fluffytime.myPage.dto.MyPageResponseDto;
import com.fluffytime.myPage.dto.PostDto;
import com.fluffytime.myPage.repository.PostRepository;
import com.fluffytime.myPage.repository.ProfileRepository;
import com.fluffytime.myPage.repository.UserRepository;
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

    // 마이페이지 사용자 조회
    @Transactional(readOnly = true)
    public Optional<User> findUser(String userId) {
        Long username = Long.parseLong(userId); // String -> Long 변환
        return userRepository.findById(username);
    }

    // 마이페이지 응답 dto 구성
    @Transactional(readOnly = true)
    public MyPageResponseDto createMyPageResponseDto(String userId) {
        User user = findUser(userId).get(); // 사용자 객체
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

        MyPageResponseDto myPageResponseDto = new MyPageResponseDto(nickName, postsList, petName,
            petSex, petAge, intro);
        return myPageResponseDto;
    }

    // 프로필 응답 dto구성

}
