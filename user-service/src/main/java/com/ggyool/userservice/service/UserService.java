package com.ggyool.userservice.service;

import com.ggyool.userservice.dto.UserDto;
import com.ggyool.userservice.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();

    UserDto getDetailsByEmail(String email);
}
