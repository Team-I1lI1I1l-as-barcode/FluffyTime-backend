package com.fluffytime.Tag.service;

import com.fluffytime.Tag.dto.request.TagRequest;
import com.fluffytime.Tag.dto.response.TagResponse;
import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.common.exception.global.PostNotFound;
import com.fluffytime.common.exception.global.TagNotFound;
import com.fluffytime.domain.Post;
import com.fluffytime.domain.Tag;
import com.fluffytime.domain.TagPost;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.TagPostRepository;
import com.fluffytime.repository.TagRepository;
import com.fluffytime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final TagPostRepository tagPostRepository;
    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;

    //태그 등록하기
    @Transactional
    public TagResponse createTag(TagRequest tagRequest) {
        // Post ID를 사용해 Post 객체 조회
        Post post = postRepository.findById(tagRequest.getPostId())
            .orElseThrow(PostNotFound::new);

        // 태그 생성
        Tag tag = Tag.builder()
            .tagName(tagRequest.getTagName())
            .build();
        tagRepository.save(tag);

        // TagPost 관계 생성
        TagPost tagPost = TagPost.builder()
            .post(post)
            .tag(tag)
            .build();
        tagPostRepository.save(tagPost);

        return TagResponse.builder()
            .tagId(tag.getTagId())
            .tagName(tag.getTagName())
            .build();
    }

    // 태그 삭제
    @Transactional
    public void deleteTagById(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(TagNotFound::new);

        tagRepository.delete(tag);
    }

}
