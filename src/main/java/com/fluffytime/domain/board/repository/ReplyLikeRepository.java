package com.fluffytime.domain.board.repository;

import com.fluffytime.domain.board.entity.Reply;
import com.fluffytime.domain.board.entity.ReplyLike;
import com.fluffytime.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {

    ReplyLike findByReplyAndUser(Reply reply, User user);

    List<ReplyLike> findAllByReply(Reply reply);

    int countByReply(Reply reply);

    boolean existsByReplyAndUserUserId(Reply reply, Long userId);
}
