package com.ggyool.userservice.controller;

import com.ggyool.userservice.dto.UserDto;
import com.ggyool.userservice.entity.UserEntity;
import com.ggyool.userservice.service.UserService;
import com.ggyool.userservice.vo.Greeting;
import com.ggyool.userservice.vo.RequestUser;
import com.ggyool.userservice.vo.ResponseUser;
import org.apache.http.HttpHeaders;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/")
public class UserController {

    private final UserService userService;
    private final Greeting greeting;
    private final ModelMapper modelMapper;
    private final Environment environment;

    public UserController(UserService userService, Greeting greeting, ModelMapper modelMapper, Environment environment) {
        this.userService = userService;
        this.greeting = greeting;
        this.modelMapper = modelMapper;
        this.environment = environment;
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers(HttpServletRequest httpServletRequest) {
        final Enumeration<String> headers = httpServletRequest.getHeaders(HttpHeaders.AUTHORIZATION);
        Iterable<UserEntity> users = userService.getUserByAll();
        List<ResponseUser> responseUsers = StreamSupport.stream(users.spliterator(), false)
                .map(userEntity -> modelMapper.map(userEntity, ResponseUser.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseUsers);
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser requestUser) {
        UserDto userRequestDto = modelMapper.map(requestUser, UserDto.class);
        UserDto userResponseDto = userService.createUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(userResponseDto, ResponseUser.class));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable String userId) {
        UserDto userDto = userService.getUserByUserId(userId);
        return ResponseEntity.ok(modelMapper.map(userDto, ResponseUser.class));
    }

    @GetMapping("/health-check")
    public String status(@Value("${local.server.port}") String port) {
        return "It's Working in User Service"
                + ", port(local.server.port)=" + port
                + ", port(server.port)=" + environment.getProperty("server.port")
                + ", token secret=" + environment.getProperty("token.secret")
                + ", token expiration time=" + environment.getProperty("token.expiration-time");
    }

    @GetMapping("/welcome")
    public String welcome() {
        return greeting.getMessage();
    }
}
