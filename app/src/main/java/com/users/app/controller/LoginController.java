package com.users.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.users.app.dto.UserResponse;
import com.users.app.service.LoginService;

@RestController
@RequestMapping("/users/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> login(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(loginService.login(authHeader));
    }

}
