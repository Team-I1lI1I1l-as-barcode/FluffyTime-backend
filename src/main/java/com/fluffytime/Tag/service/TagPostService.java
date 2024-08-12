package com.fluffytime.Tag.service;

import com.fluffytime.repository.TagPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagPostService {

    private final TagPostRepository tagPostRepository;

//    public TagPost createTagPost(Post post, Tag tag) {
//        TagPost tagPost = TagPost.builder()
//            .post(post)
//            .tag(tag)
//            .build();
//        return tagPostRepository.save(tagPost);
//    }
}
