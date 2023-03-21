package com.hyperspacegamepanel.services;

import java.util.concurrent.CompletableFuture;

import com.hyperspacegamepanel.models.user.User;


public interface TokenService {
    
    CompletableFuture<String> generateToken(User user);
    CompletableFuture<User> validateToken(String tokenValue);

}
