package com.fluffytime.repository;

import com.fluffytime.domain.Reply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByCommentCommentId(Long commentId);
}
