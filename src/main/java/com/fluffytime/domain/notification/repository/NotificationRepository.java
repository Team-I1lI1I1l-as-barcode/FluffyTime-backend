package com.fluffytime.domain.notification.repository;

import com.fluffytime.domain.notification.entity.Notification;
import com.fluffytime.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByIsReadAscCreatedAtDesc(User user);
}
