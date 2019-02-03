package com.springbootws.springbootws.ui.service;

import com.springbootws.springbootws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

public interface UserService extends UserDetailsService{

    UserDto createUser(UserDto userDto);
    UserDto getUserByEmail(String email);

    UserDto getUserByUserId(String id);

    UserDto updateUser(String id, UserDto userDto);

    void deleteUser(String userId);

    List<UserDto> getUsers(int page, int limit);

    boolean verifyByEmailToken(String token);
}
