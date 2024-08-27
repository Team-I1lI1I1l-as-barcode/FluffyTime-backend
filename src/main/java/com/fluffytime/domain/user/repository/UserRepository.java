package com.fluffytime.domain.user.repository;

import com.fluffytime.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 이름 중복 여부
    boolean existsByNickname(String nickname);

    boolean existsUserByEmail(String email);

    Optional<User> findByUserId(Long userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    List<User> findByNicknameContaining(String keyword);
}
