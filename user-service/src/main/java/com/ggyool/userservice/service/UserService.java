package com.ggyool.userservice.service;

import com.ggyool.userservice.dto.UserDto;
import com.ggyool.userservice.entity.UserEntity;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();
}
