package com.users.app.controller;


import com.users.app.dto.UserResponse;
import com.users.app.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    private LoginService loginService;
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        loginService = mock(LoginService.class);
        loginController = new LoginController(loginService);
    }

    @Test
    void testLogin_shouldReturnUserResponseFromService() {
        String token = "Bearer test.jwt.token";
        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setEmail("test@example.com");

        when(loginService.login(token)).thenReturn(expectedResponse);

        ResponseEntity<UserResponse> response = loginController.login(token);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);

        verify(loginService).login(token);
    }
}
