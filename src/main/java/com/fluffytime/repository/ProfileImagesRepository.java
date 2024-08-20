package com.fluffytime.repository;

import com.fluffytime.domain.ProfileImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileImagesRepository extends JpaRepository<ProfileImages, Long> {

}
