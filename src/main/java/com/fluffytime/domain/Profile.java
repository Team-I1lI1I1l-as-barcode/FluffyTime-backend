package com.fluffytime.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "profiles")
@Getter
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "intro", columnDefinition = "TEXT")
    private String intro;

    @Column(name = "pet_name", nullable = false, length = 100)
    private String petName;

    @Column(name = "pet_sex", nullable = false, length = 10)
    private String petSex;

    @Column(name = "pet_age", nullable = false, columnDefinition = "BIGINT")
    private Long petAge;

    @Column(name = "pet_category", nullable = false, length = 50)
    private String petCategory;

    @Column(name = "public_status", nullable = false, length = 10)
    private String publicStatus;

    @PrePersist
    public void create() {
        this.publicStatus = "1";
    }

//    @OneToOne
//    @JoinColumn(name = "user_id")
//    private User user;

}
