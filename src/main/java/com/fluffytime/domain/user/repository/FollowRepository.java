package com.fluffytime.domain.user.repository;

import com.fluffytime.domain.user.entity.Follow;
import com.fluffytime.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 팔로우 관계를 찾는 메서드
    Optional<Follow> findByFollowingUserUserIdAndFollowedUserUserId(Long followingUserId,
        Long followedUserId);

    // 특정 사용자의 팔로워 수 조회
    int countByFollowedUser(User followedUser);

    // 특정 사용자의 팔로잉 수 조회
    int countByFollowingUser(User followingUser);


}

