package com.fluffytime.like.service.post;

import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.repository.PostLikeRepository;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final JwtTokenizer jwtTokenizer;

    //게시글 좋아요 등록/취소
}
