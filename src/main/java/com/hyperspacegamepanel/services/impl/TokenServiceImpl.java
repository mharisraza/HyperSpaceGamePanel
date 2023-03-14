package com.hyperspacegamepanel.services.impl;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.helper.Token;
import com.hyperspacegamepanel.services.TokenService;


@Service
public class TokenServiceImpl implements TokenService  {

    private SecureRandom secureRandom = new SecureRandom();
    private Set<Token> tokens = new HashSet<>();

 
    @Override
    public String generateToken(User user) {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String tokenValue = Base64.getUrlEncoder().encodeToString(tokenBytes);
        
        Token token = new Token(tokenValue, user);
        tokens.add(token);

        return tokenValue;
    }

    @Override
    public boolean isTokenValid(String tokenValue) {
        Token token = getToken(tokenValue);
        return token != null && !token.isExpired();
    }

    @Override
    public User getUserByToken(String tokenValue) {
        Token token = getToken(tokenValue);
        return token != null ? token.getUser() : null;
    }

    @Override
    public Token getToken(String tokenValue) {
        for(Token token : this.tokens) {
              if(token.getToken().equals(tokenValue)) {
                return token;
              }
        }
        return null;
    }

    @Override
    @Scheduled(fixedDelay = 60000) // runs every minute
    public void removeExpiredTokens() {
        tokens.removeIf(Token::isExpired);
    }
  
}
