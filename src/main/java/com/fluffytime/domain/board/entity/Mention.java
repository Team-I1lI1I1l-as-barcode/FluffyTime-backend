package com.fluffytime.domain.board.entity;

import com.fluffytime.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "mentions")
@Entity
@NoArgsConstructor
public class Mention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mention_id", nullable = false)
    private Long mentionId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @ManyToOne
    @JoinColumn(name = "mentioned_user_id")
    private User metionedUser;

    @Builder
    public Mention(Long mentionId, Post post, Comment comment, Reply reply, User metionedUser) {
        this.mentionId = mentionId;
        this.post = post;
        this.comment = comment;
        this.reply = reply;
        this.metionedUser = metionedUser;
    }
}
