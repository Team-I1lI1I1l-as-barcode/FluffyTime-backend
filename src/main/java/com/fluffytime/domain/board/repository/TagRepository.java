package com.fluffytime.domain.board.repository;

import com.fluffytime.domain.board.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByTagName(String name);
}
