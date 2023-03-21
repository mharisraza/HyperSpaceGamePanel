package com.hyperspacegamepanel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperspacegamepanel.models.server.Server;

public interface ServerRepository extends JpaRepository<Server, Integer> {
    
}
