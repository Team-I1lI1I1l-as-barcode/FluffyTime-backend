package com.fluffytime.domain.board.repository;

import com.fluffytime.domain.board.entity.Reels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReelsRepository extends JpaRepository<Reels, Long> {
}