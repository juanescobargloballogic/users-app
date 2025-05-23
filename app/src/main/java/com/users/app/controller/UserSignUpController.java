package com.users.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.users.app.dto.UserRequest;
import com.users.app.dto.UserResponse;
import com.users.app.service.UserService;
import com.users.app.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/users/sign-up")
public class UserSignUpController {

    private final UserService userService;

    public UserSignUpController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> signUp(@Validated @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

}

