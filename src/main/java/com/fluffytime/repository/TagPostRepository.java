package com.fluffytime.repository;

import com.fluffytime.domain.TagPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagPostRepository extends JpaRepository<TagPost, Long> {

}