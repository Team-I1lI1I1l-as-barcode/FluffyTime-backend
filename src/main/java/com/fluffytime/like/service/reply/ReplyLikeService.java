package com.fluffytime.like.service.reply;

import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.common.exception.global.ReplyNotFound;
import com.fluffytime.common.exception.global.UserNotFound;
import com.fluffytime.domain.Reply;
import com.fluffytime.domain.ReplyLike;
import com.fluffytime.domain.User;
import com.fluffytime.like.dto.reply.ReplyLikeRequestDto;
import com.fluffytime.like.dto.reply.ReplyLikeResponseDto;
import com.fluffytime.repository.ReplyLikeRepository;
import com.fluffytime.repository.ReplyRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyLikeService {

    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final ReplyLikeRepository replyLikeRepository;
    private final JwtTokenizer jwtTokenizer;

    //답글 좋아요 등록/취소
    public ReplyLikeResponseDto likeOrUnlikeReply(Long replyId, ReplyLikeRequestDto requestDto) {

        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(ReplyNotFound::new);
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(UserNotFound::new);

        //좋아요 눌렀는지 안 눌렀는지 확인
        ReplyLike exisitingLike = replyLikeRepository.findByReplyAndUser(reply, user);

        boolean isLiked = false;
        if (exisitingLike != null) {
            replyLikeRepository.delete(exisitingLike); //좋아요 취소
        } else {
            ReplyLike replyLike = ReplyLike.builder()
                .reply(reply)
                .user(user)
                .build();
            replyLikeRepository.save(replyLike);
            isLiked = true;
        }

        int likeCount = replyLikeRepository.countByReply(reply); //현재 좋아요 수

        return ReplyLikeResponseDto.builder()
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .likeCount(likeCount)
            .isLiked(isLiked)
            .build();
    }

    //답글 좋아요 한 유저 목록
    public List<ReplyLikeResponseDto> getUsersWhoLikedReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(ReplyNotFound::new);

        return replyLikeRepository.findAllByReply(reply).stream()
            .map(like -> ReplyLikeResponseDto.builder()
                .userId(like.getUser().getUserId())
                .nickname(like.getUser().getNickname())
                .likeCount(replyLikeRepository.countByReply(reply))
                .isLiked(true)
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
