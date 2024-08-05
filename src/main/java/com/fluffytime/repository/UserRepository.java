package com.fluffytime.repository;

import com.fluffytime.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 이름 조회
    User findByNickname(String nickname);

    // 사용자 이름 중복 여부
    boolean existsByNickname(String nickname);
}
