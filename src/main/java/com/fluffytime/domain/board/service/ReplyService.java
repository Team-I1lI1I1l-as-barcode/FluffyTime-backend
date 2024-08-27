package com.fluffytime.domain.board.service;

import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.global.common.exception.global.CommentNotFound;
import com.fluffytime.global.common.exception.global.ReplyNotFound;
import com.fluffytime.global.common.exception.global.UserNotFound;
import com.fluffytime.domain.board.entity.Comment;
import com.fluffytime.domain.board.entity.Reply;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.board.dto.request.ReplyRequest;
import com.fluffytime.domain.board.dto.response.ReplyResponse;
import com.fluffytime.domain.board.repository.CommentRepository;
import com.fluffytime.domain.board.repository.ReplyLikeRepository;
import com.fluffytime.domain.board.repository.ReplyRepository;
import com.fluffytime.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;
    private final ReplyLikeRepository replyLikeRepository;

    //답글 저장
    public void createReply(ReplyRequest requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(UserNotFound::new);
        Comment comment = commentRepository.findById(requestDto.getCommentId())
            .orElseThrow(CommentNotFound::new); //임시 예외처리

        Reply reply = Reply.builder()
            .content(requestDto.getContent())
            .user(user)
            .comment(comment)
            .build();
        replyRepository.save(reply);
    }

    //답글 조회
    public List<ReplyResponse> getRepliesByCommentId(Long commentId, Long currentUserId) {
        List<Reply> replyList = replyRepository.findByCommentCommentId(commentId);
        return replyList.stream()
            .map(reply -> convertToReplyResponseDto(reply, currentUserId))
            .collect(Collectors.toList());
    }

    //답글 수정
    public void updateReply(Long replyId, String content) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(ReplyNotFound::new);
        reply.setContent(content);
        replyRepository.save(reply);
    }

    //답글 삭제
    public void deleteReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(ReplyNotFound::new);
        replyRepository.delete(reply);
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

    //답글 ID로 답글 조회하기
    public ReplyResponse getReplyByReplyId(Long replyId, Long currentUserId) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(ReplyNotFound::new);

        return convertToReplyResponseDto(reply, currentUserId);
    }

    //답글 response convert
    private ReplyResponse convertToReplyResponseDto(Reply reply, Long currentUserId) {
        return ReplyResponse.builder()
            .replyId(reply.getReplyId())
            .userId(reply.getUser().getUserId())
            .content(reply.getContent())
            .nickname(reply.getUser().getNickname())
            .createdAt(reply.getCreatedAt())
            .isAuthor(reply.getUser().getUserId().equals(currentUserId))
            .profileImageurl(getProfileImageUrl(reply.getUser()))
            .likeCount(reply.getLikes().size())
            .isLiked(reply.getLikes().stream()
                .anyMatch(like -> like.getUser().getUserId().equals(currentUserId)))
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
