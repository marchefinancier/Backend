package com.example.simulateurmarche.repositories;

import com.example.simulateurmarche.entities.ERole;
import com.example.simulateurmarche.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(ERole name);

}
