package com.hyperspacegamepanel.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.hyperspacegamepanel.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    User getByEmail(String email);
    boolean existsByEmail(String email);    
}
