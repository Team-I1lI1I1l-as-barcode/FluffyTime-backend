package com.fluffytime.Tag.service;

import com.fluffytime.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

//    public Tag createTag(String name) {
//        Tag tag = Tag.builder()
//            .name(name)
//            .build();
//        return tagRepository.save(tag);
//    }
}
