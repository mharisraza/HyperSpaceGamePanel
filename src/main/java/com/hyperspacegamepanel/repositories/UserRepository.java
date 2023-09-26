package com.hyperspacegamepanel.repositories;

import com.hyperspacegamepanel.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByEmail(String email);

    User getByEmail(String email);

    boolean existsByEmail(String email); 
    boolean existsByUsername(String username);
    boolean existsByUsernameOrEmail(String username, String email);
    
    List<User> findAllByRole(String role);
}
