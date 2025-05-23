package com.users.app.controller;

import com.users.app.dto.UserRequest;
import com.users.app.dto.UserResponse;
import com.users.app.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserSignUpControllerTest {

    private UserServiceImpl userService;

    private UserSignUpController userSignUpController;

    @BeforeEach
    void setUp() {
        userService = mock(UserServiceImpl.class);
        userSignUpController = new UserSignUpController(userService);
    }

    @Test
    void signUp_shouldReturnCreatedUserResponse() {
        UserRequest request = new UserRequest();
        request.setEmail("test@mail.com");
        request.setPassword("a2asfGfdfdf4");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setEmail("test@mail.com");

        when(userService.createUser(request)).thenReturn(expectedResponse);

        ResponseEntity<UserResponse> response = userSignUpController.signUp(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(userService).createUser(request);
    }
}
