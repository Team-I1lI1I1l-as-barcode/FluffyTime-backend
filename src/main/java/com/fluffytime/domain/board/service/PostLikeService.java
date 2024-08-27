package com.fluffytime.domain.board.service;

import com.fluffytime.domain.board.dto.request.PostLikeRequest;
import com.fluffytime.domain.board.dto.response.PostLikeResponse;
import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.board.entity.PostLike;
import com.fluffytime.domain.board.exception.LikeIsExists;
import com.fluffytime.domain.board.exception.NoLikeFound;
import com.fluffytime.domain.board.repository.PostLikeRepository;
import com.fluffytime.domain.board.repository.PostRepository;
import com.fluffytime.domain.user.entity.Profile;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.global.common.exception.global.PostNotFound;
import com.fluffytime.global.common.exception.global.UserNotFound;
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

    //게시글 좋아요 등록
    public PostLikeResponse likePost(Long postId, PostLikeRequest requestDto) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(UserNotFound::new);

        //좋아요를 눌렀는지 안 눌렀는지 확인
        if (postLikeRepository.findByPostAndUser(post, user) != null) {
            throw new LikeIsExists();
        }

        PostLike postLike = PostLike.builder()
            .post(post)
            .user(user)
            .build();
        postLikeRepository.save(postLike); //좋아요 등록

        int likeCount = postLikeRepository.countByPost(post); //현재 좋아요 수

        return PostLikeResponse.builder()
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .likeCount(likeCount)
            .isLiked(true)
            .build();
    }

    //게시글 좋아요 취소
    public PostLikeResponse unlikePost(Long postId, PostLikeRequest requestDto) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(UserNotFound::new);

        //좋아요를 눌럿는지 안 눌렀는지 확인
        PostLike exisitingLike = postLikeRepository.findByPostAndUser(post, user);
        if (exisitingLike == null) {
            throw new NoLikeFound();
        }

        postLikeRepository.delete(exisitingLike);

        int likeCount = postLikeRepository.countByPost(post); //현재 좋아요 수

        return PostLikeResponse.builder()
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .likeCount(likeCount)
            .isLiked(false)
            .build();
    }

    //게시글 좋아요 한 유저 목록
    public List<PostLikeResponse> getUsersWhoLikedPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        return postLikeRepository.findAllByPost(post).stream()
            .map(like -> convertToPostLikeResponseDto(like, post))
            .collect(Collectors.toList());
    }

    //accessToken으로 사용자 찾기
    @Transactional(readOnly = true)
    public User findByAccessToken(HttpServletRequest httpServletRequest) {
        String accessToken = jwtTokenizer.getTokenFromCookie(httpServletRequest, "accessToken");

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

    //게시글 좋아요 response convert
    private PostLikeResponse convertToPostLikeResponseDto(PostLike like,
        Post post) {
        return PostLikeResponse.builder()
            .userId(like.getUser().getUserId())
            .nickname(like.getUser().getNickname())
            .likeCount(postLikeRepository.countByPost(post))
            .isLiked(true)
            .profileImageurl(getProfileImageUrl(like.getUser()))
            .intro(Optional.ofNullable(like.getUser().getProfile()).map(Profile::getIntro)
                .orElse(null))
            .build();
    }

    //프로필 이미지 response convert
    private String getProfileImageUrl(User user) {
        return Optional.ofNullable(user.getProfile())
            .flatMap(profile -> Optional.ofNullable(profile.getProfileImages()))
            .map(profileImages -> profileImages.getFilePath())
            .orElse("/image/profile/profile.png");
    }
}
