package com.fluffytime.domain.user.repository;

import com.fluffytime.domain.user.entity.Profile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    List<Profile> findByPetNameContaining(String keyword);
}
