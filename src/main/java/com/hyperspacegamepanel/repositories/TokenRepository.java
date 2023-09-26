package com.hyperspacegamepanel.repositories;

import com.hyperspacegamepanel.models.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByTokenValue(String tokenValue);
    
}
