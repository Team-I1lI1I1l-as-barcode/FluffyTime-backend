package com.fluffytime.domain.board.repository;

import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.board.entity.PostLike;
import com.fluffytime.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    PostLike findByPostAndUser(Post post, User user);

    List<PostLike> findAllByPost(Post post);

    int countByPost(Post post);

    boolean existsByPostAndUserUserId(Post post, Long userId);
}
