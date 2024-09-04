package com.fluffytime.domain.board.repository;

import com.fluffytime.domain.board.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostPostId(Long postId);
}
