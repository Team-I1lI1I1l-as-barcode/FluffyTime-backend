package com.fluffytime.repository;

import com.fluffytime.domain.Comment;
import com.fluffytime.domain.CommentLike;
import com.fluffytime.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    CommentLike findByCommentAndUser(Comment comment, User user);

    List<CommentLike> findAllByComment(Comment comment);

    int countByComment(Comment comment);

    boolean existsByCommentAndUserUserId(Comment comment, Long userId);
}
