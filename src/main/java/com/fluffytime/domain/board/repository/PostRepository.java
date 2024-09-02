package com.fluffytime.domain.board.repository;

import com.fluffytime.domain.admin.dto.DailyContentsCount;
import com.fluffytime.domain.admin.dto.DailyCount;
import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.board.entity.enums.TempStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findAllByUser_UserIdAndTempStatus(Long userId, TempStatus tempStatus);

    @Query(
        value = "SELECT new com.fluffytime.domain.admin.dto.DailyContentsCount(p.createdAt, COUNT(p)) " +
            "FROM Post p " +
            "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY p.createdAt " +
            "ORDER BY p.createdAt ASC"
    )
    List<DailyCount> findPostCountByCreatedAtBetween(
      LocalDateTime startDate,
      LocalDateTime endDate
    );
}
