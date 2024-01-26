package com.example.simulateurmarche.repositories;

import com.example.simulateurmarche.entities.Action;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IActionRepository extends  JpaRepository<Action,Integer> {
}
