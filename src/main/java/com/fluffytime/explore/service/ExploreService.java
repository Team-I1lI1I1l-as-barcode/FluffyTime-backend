package com.fluffytime.explore.service;

import com.fluffytime.domain.Post;
import com.fluffytime.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExploreService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public List<Post> findLatestPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }
}
