package com.fluffytime.reply.service;

import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.common.exception.global.CommentNotFound;
import com.fluffytime.common.exception.global.ReplyNotFound;
import com.fluffytime.common.exception.global.UserNotFound;
import com.fluffytime.domain.Comment;
import com.fluffytime.domain.Reply;
import com.fluffytime.domain.User;
import com.fluffytime.reply.dto.ReplyRequestDto;
import com.fluffytime.reply.dto.ReplyResponseDto;
import com.fluffytime.repository.CommentRepository;
import com.fluffytime.repository.ReplyLikeRepository;
import com.fluffytime.repository.ReplyRepository;
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
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;
    private final ReplyLikeRepository replyLikeRepository;

    //답글 저장
    public void createReply(ReplyRequestDto requestDto) {
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
    public List<ReplyResponseDto> getRepliesByCommentId(Long commentId, Long currentUserId) {
        List<Reply> replyList = replyRepository.findByCommentCommentId(commentId);
        return replyList.stream().map(reply -> {
            int likeCount = replyLikeRepository.countByReply(reply);
            boolean isLiked = replyLikeRepository.existsByReplyAndUserUserId(reply, currentUserId);

            return ReplyResponseDto.builder()
                .replyId(reply.getReplyId())
                .userId(reply.getUser().getUserId())
                .content(reply.getContent())
                .nickname(reply.getUser().getNickname())
                .createdAt(reply.getCreatedAt())
                .isAuthor(reply.getUser().getUserId().equals(currentUserId))
                .profileImageurl(Optional.ofNullable(reply.getUser().getProfile())
                    .flatMap(profile -> Optional.ofNullable(profile.getProfileImages()))
                    .map(profileImages -> profileImages.getFilePath())
                    .orElse("/image/profile/profile.png"))
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
        }).collect(Collectors.toList());
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

    //답글 ID로 답글 조회하기
    public ReplyResponseDto getReplyByReplyId(Long replyId, Long currentUserId) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(ReplyNotFound::new);

        int likeCount = replyLikeRepository.countByReply(reply);
        boolean isLiked = replyLikeRepository.existsByReplyAndUserUserId(reply, currentUserId);

        return ReplyResponseDto.builder()
            .replyId(reply.getReplyId())
            .userId(reply.getUser().getUserId())
            .content(reply.getContent())
            .nickname(reply.getUser().getNickname())
            .createdAt(reply.getCreatedAt())
            .isAuthor(reply.getUser().getUserId().equals(currentUserId))
            .profileImageurl(Optional.ofNullable(reply.getUser().getProfile())
                .flatMap(profile -> Optional.ofNullable(profile.getProfileImages()))
                .map(profileImages -> profileImages.getFilePath())
                .orElse("/image/profile/profile.png"))
            .likeCount(likeCount)
            .isLiked(isLiked)
            .build();
    }
}
