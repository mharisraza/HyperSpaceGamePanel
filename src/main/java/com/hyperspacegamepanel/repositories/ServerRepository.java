package com.hyperspacegamepanel.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.user.User;

public interface ServerRepository extends JpaRepository<Server, Integer> {

    List<Server> findAllByOwner(User owner);
    
}
