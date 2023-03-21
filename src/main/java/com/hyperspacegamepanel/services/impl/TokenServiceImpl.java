package com.hyperspacegamepanel.services.impl;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.models.token.Token;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.TokenRepository;
import com.hyperspacegamepanel.services.TokenService;


@Service
public class TokenServiceImpl implements TokenService  {

    private final SecureRandom secureRandom = new SecureRandom();
    private final TokenRepository tokenRepo;

    @Autowired
    public TokenServiceImpl(TokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    @Override
    @Async
    public CompletableFuture<String> generateToken(User user) {
    int length = 32; // length of the random string
    String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // if you add characters like '+', '=', '-', '_', you'll need to implement url encoder/decoder logic to validate the token.
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
        int index = secureRandom.nextInt(allowedChars.length());
        char randomChar = allowedChars.charAt(index);
        sb.append(randomChar);
    }
    String token = sb.toString();
    this.tokenRepo.save(new Token(token, user));
    return CompletableFuture.completedFuture(token);
    }

    @Override
    @Async
    public CompletableFuture<User> validateToken(String tokenValue) {
        Token token = this.tokenRepo.findByTokenValue(tokenValue).orElseThrow(()-> new RuntimeException("TOKEN_IS_INVALID_OR_DOESNT_EXIST"));
        if(token.isExpired()) {
            throw new RuntimeException("TOKEN_IS_EXPIRED");
        }
        return CompletableFuture.completedFuture(token.getUser());
    }

    
    
  
}
