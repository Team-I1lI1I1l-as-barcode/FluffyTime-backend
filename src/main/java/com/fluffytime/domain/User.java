package com.fluffytime.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Table(name = "users")
@Entity
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    @Column(name = "registration_at", nullable = false)
    private LocalDateTime registrationAt;

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Post> postList = new ArrayList<>();

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Reply> replyList = new ArrayList<>();

    // 유저 - 프로필 (일대일 단방향 -> 양방향으로 수정(프로필의 반려동물 이름을 기반으로 아이디를 찾아야 하기 때문)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    private Set<UserRole> userRoles = new HashSet<>();

    // 내가 팔로우한 사용자들 (내가 followingUser인 경우)
    @OneToMany(mappedBy = "followingUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude  // 순환 참조 방지
    @EqualsAndHashCode.Exclude // 순환 참조 방지
    private List<Follow> followingList = new ArrayList<>();

    // 나를 팔로우한 사용자들 (내가 followedUser인 경우)
    @OneToMany(mappedBy = "followedUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude  // 순환 참조 방지
    @EqualsAndHashCode.Exclude // 순환 참조 방지
    private List<Follow> followerList = new ArrayList<>();

    @PrePersist
    public void create() {
        this.registrationAt = LocalDateTime.now();
    }

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
