package com.hyperspacegamepanel.helper;


import com.hyperspacegamepanel.entities.User;

import lombok.Data;

@Data
public class Token {

    private final String token;
    private final User user;
    private final long expirationTime;
    private boolean isExpired = false;

    public Token(String token, User user) {
        this.token = token;
        this.user = user;
        this.expirationTime = System.currentTimeMillis() + 30 * 60 * 1000;
    }

    public boolean isExpired() {
        return isExpired || System.currentTimeMillis() > expirationTime;
    }

    public void expire() {
      isExpired = true;
    }
       
}
