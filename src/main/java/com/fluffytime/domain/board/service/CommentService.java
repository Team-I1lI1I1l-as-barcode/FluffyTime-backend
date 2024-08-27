package com.fluffytime.domain.board.service;

import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.domain.board.dto.request.CommentRequestDto;
import com.fluffytime.domain.board.dto.response.CommentResponseDto;
import com.fluffytime.global.common.exception.global.CommentNotFound;
import com.fluffytime.global.common.exception.global.PostNotFound;
import com.fluffytime.global.common.exception.global.UserNotFound;
import com.fluffytime.domain.board.entity.Comment;
import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.board.entity.Reply;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.board.dto.response.ReplyResponse;
import com.fluffytime.domain.board.repository.CommentLikeRepository;
import com.fluffytime.domain.board.repository.CommentRepository;
import com.fluffytime.domain.board.repository.PostRepository;
import com.fluffytime.domain.user.repository.UserRepository;
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
    private final CommentLikeRepository commentLikeRepository;

    //댓글 저장
    public void createComment(CommentRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(UserNotFound::new);
        Post post = postRepository.findById(requestDto.getPostId())
            .orElseThrow(PostNotFound::new);

        Comment comment = Comment.builder()
            .content(requestDto.getContent())
            .user(user)
            .post(post)
            .build();
        commentRepository.save(comment);
    }

    //댓글 조회 - 게시글마다
    public List<CommentResponseDto> getCommentByPostId(Long postId, Long currentUserId) {
        List<Comment> commentList = commentRepository.findByPostPostId(postId);
        return commentList.stream()
            .map(comment -> convertToCommentResponseDto(comment, currentUserId))
            .collect(Collectors.toList());
    }

    //댓글 수정
    public void updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(CommentNotFound::new);

        comment.setContent(content);
        commentRepository.save(comment);
    }

    //댓글 삭제
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(CommentNotFound::new);
        commentRepository.delete(comment);
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

    //댓글 ID로 댓글 조회하기 - 수정 및 삭제
    public CommentResponseDto getCommentByCommentId(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(CommentNotFound::new);

        return convertToCommentResponseDto(comment, currentUserId);
    }

    //댓글 response convert
    private CommentResponseDto convertToCommentResponseDto(Comment comment, Long currentUserId) {
        int likeCount = commentLikeRepository.countByComment(comment);
        boolean isLiked = commentLikeRepository.existsByCommentAndUserUserId(comment,
            currentUserId);

        return CommentResponseDto.builder()
            .commentId(comment.getCommentId())
            .userId(comment.getUser().getUserId())
            .content(comment.getContent())
            .nickname(comment.getUser().getNickname())
            .createdAt(comment.getCreatedAt())
            .replyList(comment.getReplyList().stream()
                .map(reply -> convertToReplyResponseDto(reply, currentUserId))
                .collect(Collectors.toList()))
            .isAuthor(comment.getUser().getUserId().equals(currentUserId))
            .profileImageurl(getProfileImageUrl(comment.getUser()))
            .likeCount(likeCount)
            .isLiked(isLiked)
            .build();
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