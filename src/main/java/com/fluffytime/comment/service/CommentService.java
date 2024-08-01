package com.fluffytime.comment.service;

import com.fluffytime.comment.dto.CommentRequestDto;
import com.fluffytime.comment.dto.CommentResponseDto;
import com.fluffytime.comment.repository.CommentRepository;
import com.fluffytime.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    //댓글 저장
    public CommentResponseDto createComment(CommentRequestDto requestDto) {
        Comment comment = new Comment(requestDto.getContent(), requestDto.getUserId(),
            requestDto.getPostId());
        Comment savedComment = commentRepository.save(comment);
        return new CommentResponseDto(savedComment.getCommentId(), savedComment.getContent(),
            savedComment.getUser().getNickname(), savedComment.getCreatedAt());
    }

}
