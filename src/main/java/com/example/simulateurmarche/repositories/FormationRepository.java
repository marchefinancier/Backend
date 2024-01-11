package com.example.simulateurmarche.repositories;

import com.example.simulateurmarche.entities.Formation;
import com.example.simulateurmarche.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FormationRepository extends JpaRepository<Formation,Integer> {

    @Query(value = "SELECT * FROM Formation f WHERE f.user_id = :userId", nativeQuery = true)
    List<Formation> findFormationsByUserIdNative(@Param("userId") int userId);
}
