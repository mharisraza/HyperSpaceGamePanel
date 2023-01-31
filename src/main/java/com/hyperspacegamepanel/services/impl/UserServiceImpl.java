package com.hyperspacegamepanel.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.dtos.UserDto;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = this.mapper.map(userDto, User.class);

        user.setRole("ROLE_NORMAL");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User createdUser = this.userRepo.save(user);
        return this.mapper.map(createdUser, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        return null;
    }

    @Override
    public void deleteUser(Integer userId) {        
    }

    @Override
    public UserDto getUser(Integer userId) {
        return null;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return null;
    }
    
}
