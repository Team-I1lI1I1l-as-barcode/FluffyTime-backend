package com.fluffytime.reply.service;

import com.fluffytime.common.exception.global.NotFoundComment;
import com.fluffytime.common.exception.global.NotFoundReply;
import com.fluffytime.common.exception.global.NotFoundUser;
import com.fluffytime.domain.Comment;
import com.fluffytime.domain.Reply;
import com.fluffytime.domain.User;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import com.fluffytime.reply.dto.ReplyRequestDto;
import com.fluffytime.reply.dto.ReplyResponseDto;
import com.fluffytime.repository.CommentRepository;
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

    //답글 저장
    public void createReply(ReplyRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(NotFoundUser::new);
        Comment comment = commentRepository.findById(requestDto.getCommentId())
            .orElseThrow(NotFoundComment::new); //임시 예외처리

        Reply reply = Reply.builder()
            .content(requestDto.getContent())
            .user(user)
            .comment(comment)
            .build();
        replyRepository.save(reply);
    }

    //답글 조회
    public List<ReplyResponseDto> getRepliesByCommentId(Long commentId) {
        List<Reply> replyList = replyRepository.findByCommentCommentId(commentId);
        return replyList.stream()
            .map(ReplyResponseDto::new)
            .collect(Collectors.toList());
    }

    //답글 수정
    public void updateReply(Long replyId, String content) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(NotFoundReply::new);
        reply.setContent(content);
        replyRepository.save(reply);
    }

    //답글 삭제
    public void deleteReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(NotFoundReply::new);
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
        User user = findUserById(userId).orElseThrow(NotFoundUser::new);
        return user;
    }

    //사용자 조회
    public Optional<User> findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user;
    }

    //답글 ID로 답글 조회하기
    public ReplyResponseDto getReplyByReplyId(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(NotFoundReply::new);
        return new ReplyResponseDto(reply);
    }
}
