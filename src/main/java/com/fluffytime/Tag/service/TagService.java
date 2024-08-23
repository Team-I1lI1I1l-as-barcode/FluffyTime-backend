package com.fluffytime.Tag.service;

import com.fluffytime.Tag.dto.request.TagRequest;
import com.fluffytime.Tag.dto.response.TagResponse;
import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.common.exception.global.TagNotFound;
import com.fluffytime.domain.Tag;
import com.fluffytime.repository.TagPostRepository;
import com.fluffytime.repository.TagRepository;
import com.fluffytime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagPostRepository tagPostRepository;
    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;

    //태그 등록하기
    public TagResponse createTag(TagRequest tagRequest) {
        String tagName = tagRequest.getTagName();

        // 기존 태그가 있는지 확인하고, 없으면 새로 생성
        Tag tag = tagRepository.findByTagName(tagName)
            .orElseGet(() -> tagRepository.save(new Tag(tagName)));

        return TagResponse.builder()
            .tagId(tag.getTagId())
            .tagName(tag.getTagName())
            .build();
    }

    //태그 삭제하기
    public void deleteTag(String tagName) {
        Tag tag = tagRepository.findByTagName(tagName)
            .orElseThrow(TagNotFound::new);

        // 태그와 관련된 모든 TagPost 삭제
        tagPostRepository.deleteAllByTag(tag);

        // 태그 삭제
        tagRepository.delete(tag);
    }
}
