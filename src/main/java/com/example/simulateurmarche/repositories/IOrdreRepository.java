package com.example.simulateurmarche.repositories;


import com.example.simulateurmarche.entities.Ordre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrdreRepository extends JpaRepository<Ordre,Integer> {
}
