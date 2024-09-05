package com.fluffytime.domain.notification.repository;

import com.fluffytime.domain.notification.entity.AdminNotification;
import com.fluffytime.domain.notification.entity.enums.AdminNotificationType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {
    List<AdminNotification> findByTypeOrderByCreatedAtAsc(AdminNotificationType type);

    @Modifying
    @Query("UPDATE AdminNotification a "
        + "SET a.user = null "
        + "WHERE a.user.userId = :userId")
    void updateUserIdToNull(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE AdminNotification a "
        + "SET a.post = null "
        + "WHERE a.post.postId = :postId")
    void updatePostIdToNull(@Param("postId") Long postId);
}
