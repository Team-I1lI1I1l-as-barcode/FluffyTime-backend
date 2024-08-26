package com.fluffytime.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "intro", columnDefinition = "TEXT")
    private String intro;

    @Column(name = "pet_name", length = 100)
    private String petName;

    @Column(name = "pet_sex", length = 10)
    private String petSex;

    @Column(name = "pet_age", columnDefinition = "BIGINT")
    private Long petAge;

    @Column(name = "pet_category", length = 50)
    private String petCategory;

    @Column(name = "public_status", nullable = false, length = 10)
    private String publicStatus;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 프로필 - 프로필 사진 (일대일 단방향)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_image_id")
    private ProfileImages profileImages;


    @PrePersist
    public void create() {
        this.publicStatus = "1";
    }

    public Profile(String petSex, Long petAge, String petCategory) {
        this.petSex = petSex;
        this.petAge = petAge;
        this.petCategory = petCategory;
    }
}
