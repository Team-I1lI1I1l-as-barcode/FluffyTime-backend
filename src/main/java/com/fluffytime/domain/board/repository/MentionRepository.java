package com.fluffytime.domain.board.repository;

import com.fluffytime.domain.board.entity.Mention;
import com.fluffytime.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentionRepository extends JpaRepository<Mention, Long> {

    Mention findTopByOrderByMentionIdDesc();

    List<Mention> findByMetionedUserAndPostIsNotNull(User user);
}
