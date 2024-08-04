package com.fluffytime.comment.service;

import com.fluffytime.comment.dto.CommentRequestDto;
import com.fluffytime.comment.repository.CommentRepository;
import com.fluffytime.domain.Comment;
import com.fluffytime.domain.Post;
import com.fluffytime.domain.User;
import com.fluffytime.post.repository.PostRepository;
import com.fluffytime.user.repository.UserRepository;
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
            .orElseThrow(() -> new RuntimeException("user not found"));
        Post post = postRepository.findById(requestDto.getPostId())
            .orElseThrow(() -> new RuntimeException("post not found"));

        Comment comment = new Comment(requestDto.getContent(), user, post);
        commentRepository.save(comment);
    }

}
