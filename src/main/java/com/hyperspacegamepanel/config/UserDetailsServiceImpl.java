package com.hyperspacegamepanel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         User user = this.userRepo.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User with email:- "+username)); 
        return new CustomUserDetails(user);
    }
    
}
