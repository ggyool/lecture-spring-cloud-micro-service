package com.ggyool.userservice.controller;

import com.ggyool.userservice.dto.UserDto;
import com.ggyool.userservice.service.UserService;
import com.ggyool.userservice.vo.Greeting;
import com.ggyool.userservice.vo.RequestUser;
import com.ggyool.userservice.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {

    private final UserService userService;
    private final Greeting greeting;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, Greeting greeting, ModelMapper modelMapper) {
        this.userService = userService;
        this.greeting = greeting;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser requestUser) {
        UserDto userRequestDto = modelMapper.map(requestUser, UserDto.class);
        UserDto userResponseDto = userService.createUser(userRequestDto);
        ResponseUser responseUser = modelMapper.map(userResponseDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/health-check")
    public String status() {
        return "It's Working in User Service";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return greeting.getMessage();
    }
}
