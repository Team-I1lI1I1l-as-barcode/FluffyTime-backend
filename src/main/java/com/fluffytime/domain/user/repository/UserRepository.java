package com.fluffytime.domain.user.repository;

import com.fluffytime.domain.admin.dto.DailyCount;
import com.fluffytime.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 이름 중복 여부
    boolean existsByNickname(String nickname);

    boolean existsUserByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    List<User> findByNicknameContaining(String keyword);

    Page<User> findAll(Pageable pageable);

    @Query(
        value = "SELECT new com.fluffytime.domain.admin.dto.DailyUserCount(u.registrationAt, COUNT(u)) " +
            "FROM User u " +
            "WHERE u.registrationAt BETWEEN :startDate AND :endDate " +
            "GROUP BY u.registrationAt " +
            "ORDER BY u.registrationAt ASC"
    )
    List<DailyCount> findUserCountByRegistrationAtBetween(
        LocalDateTime startDate,
        LocalDateTime endDate
    );
}
