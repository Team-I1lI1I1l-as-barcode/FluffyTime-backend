package com.fluffytime.domain.board.repository;

import com.fluffytime.domain.board.entity.Bookmark;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 특정 사용자(userId)가 북마크한 모든 게시물을 조회
    List<Bookmark> findByUserUserId(Long userId);

    // 특정 게시물(postId)을 북마크한 모든 사용자를 조회
    List<Bookmark> findByPostPostId(Long postId);

    // 특정 사용자(userId)가 특정 게시물(postId)을 북마크했는지 여부를 확인
    boolean existsByUserUserIdAndPostPostId(Long userId, Long postId);
}
