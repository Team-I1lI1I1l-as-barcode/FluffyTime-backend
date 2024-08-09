package com.fluffytime.comment.service;

import com.fluffytime.comment.dto.CommentRequestDto;
import com.fluffytime.comment.dto.CommentResponseDto;
import com.fluffytime.common.exception.global.NotFoundComment;
import com.fluffytime.common.exception.global.NotFoundPost;
import com.fluffytime.common.exception.global.NotFoundUser;
import com.fluffytime.domain.Comment;
import com.fluffytime.domain.Post;
import com.fluffytime.domain.User;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import com.fluffytime.repository.CommentRepository;
import com.fluffytime.repository.PostRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;

    //댓글 저장
    public void createComment(CommentRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(NotFoundUser::new);
        Post post = postRepository.findById(requestDto.getPostId())
            .orElseThrow(NotFoundPost::new);

        Comment comment = Comment.builder()
            .content(requestDto.getContent())
            .user(user)
            .post(post)
            .build();
        commentRepository.save(comment);
    }

    //댓글 조회
    public List<CommentResponseDto> getCommentByPostId(Long postId) {
        List<Comment> commentList = commentRepository.findByPostPostId(postId);
        return commentList.stream()
            .map(CommentResponseDto::new)
            .collect(Collectors.toList());
    }

    //댓글 수정
    public void updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(NotFoundComment::new);

        comment.setContent(content);
        commentRepository.save(comment);
    }

    //댓글 삭제
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(NotFoundComment::new);
        commentRepository.delete(comment);
    }

    //accessToken으로 사용자 찾기
    @Transactional(readOnly = true)
    public User findByAccessToken(HttpServletRequest httpServletRequest) {
        log.info("findByAccessToken 실행");
        String accessToken = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    log.info("accessToken: " + accessToken);
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

    //댓글 ID로 댓글 조회하기
    public CommentResponseDto getCommentByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(NotFoundComment::new);
        return new CommentResponseDto(comment);
    }
}
