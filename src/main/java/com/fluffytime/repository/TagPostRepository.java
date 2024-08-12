package com.fluffytime.repository;

import com.fluffytime.domain.Post;
import com.fluffytime.domain.TagPost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TagPostRepository extends JpaRepository<TagPost, Long> {

    @Transactional
    void deleteByPost(Post post);

    List<TagPost> findByPost(Post post);
}