package com.hyperspacegamepanel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hyperspacegamepanel.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    
}
