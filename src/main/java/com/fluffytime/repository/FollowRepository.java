package com.fluffytime.repository;

import com.fluffytime.domain.Follow;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 팔로우 관계를 찾는 메서드
    Optional<Follow> findByFollowingUserUserIdAndFollowedUserUserId(Long followingUserId,
        Long followedUserId);
}

