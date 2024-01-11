package com.example.simulateurmarche.repositories;


import com.example.simulateurmarche.DTO.CountType;
import com.example.simulateurmarche.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    User findByUsername(String username);
    User findByName(String Name);
    List<User> findByAddress(String address);
    List<User> findByStateuser (boolean stateUser);
    List<User> findAllByOrderByRolesDesc();


    @Query("SELECT MIN(e.datenaissance) FROM User e ")
    Date getminage();
    @Query("SELECT MAX(e.datenaissance) FROM User e ")
    Date getmaxage();

    @Query(value="SELECT new  com.example.simulateurmarche.DTO.CountType(count(*),stateuser) from User  group by stateuser")
    List<CountType> statistque();
    @Query("SELECT new  com.example.simulateurmarche.DTO.CountType(count(*), u.datenaissance,u.stateuser) FROM User u GROUP BY u.datenaissance ORDER BY u.datenaissance ASC")

    List<CountType>minmaxeage();
    @Query("SELECT new  com.example.simulateurmarche.DTO.CountType(count(*), u.address) FROM User u GROUP BY u.address ORDER BY u.address ASC")

    List<CountType>adresse();
    @Query("SELECT e,COUNT(e.stateuser) FROM User e WHERE e.stateuser=false")
    List<User> getusersdisable();








}