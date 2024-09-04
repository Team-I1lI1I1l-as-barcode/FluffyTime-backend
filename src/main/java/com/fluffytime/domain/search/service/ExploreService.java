package com.fluffytime.domain.search.service;

import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.board.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExploreService {

    private final PostRepository postRepository;

    @Transactional
    public List<Post> findLatestPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }
}
