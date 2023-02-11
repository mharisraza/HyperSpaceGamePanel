package com.hyperspacegamepanel.services;

import java.util.List;

import com.hyperspacegamepanel.dtos.UserDto;
import com.hyperspacegamepanel.entities.User;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Integer userId);

    void deleteUser(Integer userId);

    UserDto getUser(Integer userId);

    User suspendUser(User user);

    User unbanUser(User user);
    
}
