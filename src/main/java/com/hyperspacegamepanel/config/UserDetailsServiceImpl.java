package com.hyperspacegamepanel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
         User user = this.userRepo.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(()-> new UsernameNotFoundException("Username or email with:- "+usernameOrEmail)); 
        return new CustomUserDetails(user);
    }
    
}
