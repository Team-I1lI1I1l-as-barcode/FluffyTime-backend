package com.fluffytime.repository;

import com.fluffytime.domain.Post;
import com.fluffytime.domain.PostLike;
import com.fluffytime.domain.User;
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
