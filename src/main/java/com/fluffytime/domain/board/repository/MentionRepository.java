package com.fluffytime.domain.board.repository;

import com.fluffytime.domain.board.entity.Mention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentionRepository extends JpaRepository<Mention, Long> {

    Mention findTopByOrderByMentionIdDesc();
}
