package com.fluffytime.domain.board.service;

import com.fluffytime.domain.board.dto.request.ReplyLikeRequest;
import com.fluffytime.domain.board.dto.response.ReplyLikeResponse;
import com.fluffytime.domain.board.entity.Reply;
import com.fluffytime.domain.board.entity.ReplyLike;
import com.fluffytime.domain.board.exception.LikeIsExists;
import com.fluffytime.domain.board.exception.NoLikeFound;
import com.fluffytime.domain.board.repository.ReplyLikeRepository;
import com.fluffytime.domain.board.repository.ReplyRepository;
import com.fluffytime.domain.notification.service.NotificationService;
import com.fluffytime.domain.user.entity.Profile;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.global.common.exception.global.ReplyNotFound;
import com.fluffytime.global.common.exception.global.UserNotFound;
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
    private final NotificationService notificationService;

    //답글 좋아요 등록
    public ReplyLikeResponse likeReply(Long replyId, ReplyLikeRequest requestDto) {

        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(ReplyNotFound::new);
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(UserNotFound::new);

        //좋아요 눌렀는지 안 눌렀는지 확인
        if (replyLikeRepository.findByReplyAndUser(reply, user) != null) {
            throw new LikeIsExists();
        }

        ReplyLike replyLike = ReplyLike.builder()
            .reply(reply)
            .user(user)
            .build();
        replyLikeRepository.save(replyLike);

        // 알림 생성 및 전송
        notificationService.createLikesNotification(reply, replyLike.getUser());

        int likeCount = replyLikeRepository.countByReply(reply); //현재 좋아요 수

        return ReplyLikeResponse.builder()
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .likeCount(likeCount)
            .isLiked(true)
            .build();
    }

    //답글 좋아요 취소
    public ReplyLikeResponse unlikeReply(Long replyId,
        ReplyLikeRequest requestDto) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(ReplyNotFound::new);
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(UserNotFound::new);

        //좋아요를 눌렀는지 안 눌렀는지 확인
        ReplyLike exisitingLike = replyLikeRepository.findByReplyAndUser(reply, user);
        if (exisitingLike == null) {
            throw new NoLikeFound();
        }

        replyLikeRepository.delete(exisitingLike);

        int likeCount = replyLikeRepository.countByReply(reply); //현재 좋아요 수

        return ReplyLikeResponse.builder()
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .likeCount(likeCount)
            .isLiked(false)
            .build();
    }

    //답글 좋아요 한 유저 목록
    public List<ReplyLikeResponse> getUsersWhoLikedReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(ReplyNotFound::new);

        return replyLikeRepository.findAllByReply(reply).stream()
            .map(like -> convertToReplyLikeResponseDto(like, reply))
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

    //답글 좋아요 response convert
    private ReplyLikeResponse convertToReplyLikeResponseDto(ReplyLike like,
        Reply reply) {
        return ReplyLikeResponse.builder()
            .userId(like.getUser().getUserId())
            .nickname(like.getUser().getNickname())
            .likeCount(replyLikeRepository.countByReply(reply))
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
