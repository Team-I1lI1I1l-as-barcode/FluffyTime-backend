package com.fluffytime.like.service;

import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.common.exception.global.CommentNotFound;
import com.fluffytime.common.exception.global.UserNotFound;
import com.fluffytime.domain.Comment;
import com.fluffytime.domain.CommentLike;
import com.fluffytime.domain.User;
import com.fluffytime.like.dto.CommentLikeRequestDto;
import com.fluffytime.like.dto.CommentLikeResponseDto;
import com.fluffytime.repository.CommentLikeRepository;
import com.fluffytime.repository.CommentRepository;
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
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final JwtTokenizer jwtTokenizer;

    //댓글 좋아요 등록/좋아요 취소
    public CommentLikeResponseDto likeOrUnlikeComment(Long commentId,
        CommentLikeRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(CommentNotFound::new);
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(UserNotFound::new);

        //좋아요를 눌렀는지 안 눌렀는지 확인
        CommentLike exisitingLike = commentLikeRepository.findByCommentAndUser(comment, user);

        boolean isLiked = false;
        if (exisitingLike != null) {
            commentLikeRepository.delete(exisitingLike); //좋아요 취소
        } else {
            CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();
            commentLikeRepository.save(commentLike); //좋아요 등록
            isLiked = true;
        }

        int likeCount = commentLikeRepository.countByComment(comment); //현재 좋아요 수

        return CommentLikeResponseDto.builder()
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .likeCount(likeCount)
            .isLiked(isLiked)
            .build();
    }

    //댓글 좋아요 한 유저 목록
    public List<CommentLikeResponseDto> getUsersWhoLikedComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(CommentNotFound::new);

        return commentLikeRepository.findAllByComment(comment).stream()
            .map(like -> CommentLikeResponseDto.builder()
                .userId(like.getUser().getUserId())
                .nickname(like.getUser().getNickname())
                .likeCount(commentLikeRepository.countByComment(comment))
                .isLiked(true) //좋아요 목록이므로 항상 true
                .profileImageurl(Optional.ofNullable(like.getUser().getProfile())
                    .map(profile -> profile.getProfileImages().getFilePath())
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
