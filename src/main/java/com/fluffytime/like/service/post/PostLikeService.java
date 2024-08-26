package com.fluffytime.like.service.post;

import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.common.exception.global.PostNotFound;
import com.fluffytime.common.exception.global.UserNotFound;
import com.fluffytime.domain.Post;
import com.fluffytime.domain.PostLike;
import com.fluffytime.domain.User;
import com.fluffytime.like.dto.post.PostLikeRequestDto;
import com.fluffytime.like.dto.post.PostLikeResponseDto;
import com.fluffytime.repository.PostLikeRepository;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final JwtTokenizer jwtTokenizer;

    //게시글 좋아요 등록/취소
    public PostLikeResponseDto likeOrUnlikePost(Long postId, PostLikeRequestDto requestDto) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(UserNotFound::new);

        //좋아요를 눌렀는지 안 눌렀는지 확인
        PostLike exisitingLike = postLikeRepository.findByPostAndUser(post, user);

        boolean isLiked = false;
        if (exisitingLike != null) {
            postLikeRepository.delete(exisitingLike); //좋아요 취소
        } else {
            PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();
            postLikeRepository.save(postLike); //좋아요 등록
            isLiked = true;
        }

        int likeCount = postLikeRepository.countByPost(post); //현재 좋아요 수

        return PostLikeResponseDto.builder()
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .likeCount(likeCount)
            .isLiked(isLiked)
            .build();
    }

    //게시글 좋아요 한 유저 목록
    public List<PostLikeResponseDto> getUsersWhoLikedPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        return postLikeRepository.findAllByPost(post).stream()
            .map(like -> PostLikeResponseDto.builder()
                .userId(like.getUser().getUserId())
                .nickname(like.getUser().getNickname())
                .likeCount(postLikeRepository.countByPost(post))
                .isLiked(true)
                .profileImageurl(Optional.ofNullable(like.getUser().getProfile())
                    .flatMap(profile -> Optional.ofNullable(profile.getProfileImages()))
                    .map(profileImages -> profileImages.getFilePath())
                    .orElse("/image/profile/profile.png"))
                .intro(like.getUser().getProfile().getIntro())
                .build())
            .collect(Collectors.toList());
    }

    //accessToken으로 사용자 찾기
    @Transactional(readOnly = true)
    public User findByAccessToken(HttpServletRequest httpServletRequest) {
        String accessToken = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

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
}
