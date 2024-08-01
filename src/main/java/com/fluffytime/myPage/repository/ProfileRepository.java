package com.fluffytime.myPage.repository;

import com.fluffytime.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

}
