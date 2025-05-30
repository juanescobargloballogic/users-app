package com.users.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.users.app.dto.UserResponse;
import com.users.app.service.LoginService;

/**
 * Handles user login requests.
 * The endpoint on this controller requires a valid JWT token in the Authorization header.
 */
@RestController
@RequestMapping("/users/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Retrieves the authenticated user's data and refreshes their login token.
     * Requires a valid token in the authorization header.
     *
     * @return full user details including refreshed JWT token.
     */
    @PostMapping
    public ResponseEntity<UserResponse> login() {
        return ResponseEntity.ok(loginService.login());
    }

}
