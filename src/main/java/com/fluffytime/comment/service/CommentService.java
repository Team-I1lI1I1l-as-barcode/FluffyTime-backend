package com.fluffytime.comment.service;

import com.fluffytime.comment.dto.CommentRequestDto;
import com.fluffytime.comment.dto.CommentResponseDto;
import com.fluffytime.comment.exception.CommentNotFoundException;
import com.fluffytime.comment.exception.PostNotFoundException;
import com.fluffytime.comment.exception.UserNotFoundException;
import com.fluffytime.domain.Comment;
import com.fluffytime.domain.Post;
import com.fluffytime.domain.User;
import com.fluffytime.repository.CommentRepository;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //댓글 저장
    public void createComment(CommentRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(requestDto.getPostId())
            .orElseThrow(PostNotFoundException::new);

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
            .orElseThrow(CommentNotFoundException::new);
        comment.setContent(content);
        commentRepository.save(comment);
    }

    //댓글 삭제
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(CommentNotFoundException::new);
        commentRepository.delete(comment);
    }
}
