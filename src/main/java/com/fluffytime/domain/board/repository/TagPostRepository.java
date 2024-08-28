package com.fluffytime.domain.board.repository;

import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.board.entity.TagPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagPostRepository extends JpaRepository<TagPost, Long> {
    void deleteAllByPost(Post post);
}
