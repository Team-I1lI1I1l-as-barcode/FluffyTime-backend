package com.fluffytime.domain.board.repository;

import com.fluffytime.domain.board.entity.Reply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByCommentCommentId(Long commentId);
}
