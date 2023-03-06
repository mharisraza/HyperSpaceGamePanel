package com.hyperspacegamepanel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperspacegamepanel.entities.Server;

public interface ServerRepository extends JpaRepository<Server, Integer> {
    
}
