package com.fluffytime.domain.user.repository;

import com.fluffytime.domain.user.entity.ProfileImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileImagesRepository extends JpaRepository<ProfileImages, Long> {

}
