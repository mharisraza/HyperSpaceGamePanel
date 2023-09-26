package com.hyperspacegamepanel.repositories;

import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Integer> {

    List<Server> findAllByOwner(User owner);
    
}
