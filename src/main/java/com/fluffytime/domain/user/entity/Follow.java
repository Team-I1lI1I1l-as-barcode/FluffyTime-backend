package com.fluffytime.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
//한 유저가 동일한 유저를 중복 팔로우하는 것을 방지
@Table(name = "follows", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"following_user_id", "followed_user_id"})})
@Getter
@Setter
@NoArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_mapping_id", nullable = false)
    private long followMappingId;

    // 팔로잉하는 사용자
    @ManyToOne
    @JoinColumn(name = "following_user_id", nullable = false)
    @ToString.Exclude  // 순환 참조 방지
    @EqualsAndHashCode.Exclude // 순환 참조 방지
    private User followingUser;

    // 팔로우당하는 사용자
    @ManyToOne
    @JoinColumn(name = "followed_user_id", nullable = false)
    @ToString.Exclude  // 순환 참조 방지
    @EqualsAndHashCode.Exclude // 순환 참조 방지
    private User followedUser;
}
