package com.fluffytime.domain.board.service;

import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.board.entity.Tag;
import com.fluffytime.domain.board.entity.TagPost;
import com.fluffytime.domain.board.repository.TagPostRepository;
import com.fluffytime.domain.board.repository.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagPostRepository tagPostRepository;

    @Transactional
    public void regTags(List<String> tags, Post post) {
        for (String name : tags) {
            Tag tag = tagRepository.findByTagName(name);
            if (tag == null) {
                tag = Tag.builder()
                .tagName(name)
                .build();

                tagRepository.save(tag);
            }

            TagPost tagPost = TagPost.builder()
                .tag(tag)
                .post(post)
                .build();

            post.getTagPosts().add(tagPost);
            tag.getTagPosts().add(tagPost);

            tagPostRepository.save(tagPost);
        }
    }
}
