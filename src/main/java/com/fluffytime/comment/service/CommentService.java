package com.fluffytime.comment.service;

import com.fluffytime.comment.config.error.exception.PostNotFoundException;
import com.fluffytime.comment.config.error.exception.UserNotFoundException;
import com.fluffytime.comment.dto.CommentRequestDto;
import com.fluffytime.domain.Comment;
import com.fluffytime.domain.Post;
import com.fluffytime.domain.User;
import com.fluffytime.repository.CommentRepository;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.UserRepository;
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

        Comment comment = new Comment(requestDto.getContent(), user, post);
        commentRepository.save(comment);
    }

}
