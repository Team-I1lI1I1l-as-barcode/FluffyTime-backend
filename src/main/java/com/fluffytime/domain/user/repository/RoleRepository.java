package com.fluffytime.domain.user.repository;

import com.fluffytime.domain.user.entity.Role;
import com.fluffytime.domain.user.entity.enums.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName name);
}
