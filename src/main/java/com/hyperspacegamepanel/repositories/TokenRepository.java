package com.hyperspacegamepanel.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperspacegamepanel.models.token.Token;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByTokenValue(String tokenValue);
    
}
