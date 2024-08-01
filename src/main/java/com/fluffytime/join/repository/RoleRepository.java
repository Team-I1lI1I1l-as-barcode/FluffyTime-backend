package com.fluffytime.join.repository;

import com.fluffytime.domain.Role;
import com.fluffytime.domain.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName name);
}
