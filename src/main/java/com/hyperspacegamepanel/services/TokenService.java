package com.hyperspacegamepanel.services;

import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.helper.Token;


public interface TokenService {
    
    String generateToken(User user);
    boolean isTokenValid(String tokenValue);
    User getUserByToken(String tokenValue);
    Token getToken(String tokenValue);

    void removeExpiredTokens(); 

}
