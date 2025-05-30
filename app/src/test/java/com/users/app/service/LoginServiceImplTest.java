package com.users.app.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.users.app.dto.AuthenticatedUser;
import com.users.app.dto.UserResponse;
import com.users.app.entity.PhoneEntity;
import com.users.app.entity.UserEntity;
import com.users.app.exception.NotFoundException;
import com.users.app.exception.UnauthorizedException;
import com.users.app.util.JwtUtil;
import com.users.app.repository.PhoneRepository;
import com.users.app.repository.UserRepository;
import com.users.app.service.impl.LoginServiceImpl;
import com.users.app.testdata.AuthenticatedUserObjectMother;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.users.app.testdata.PhoneEntityObjectMother.*;
import static com.users.app.testdata.UserEntityObjectMother.defaultUser;
import static com.users.app.testdata.UserEntityObjectMother.inactiveUser;

public class LoginServiceImplTest {

    private LoginService loginService;

    private UserRepository userRepository;

    private PhoneRepository phoneRepository;

    private JwtUtil jwtUtil;

    private static final String EMAIL = "name@email.com";

    private static final AuthenticatedUser AUTHENTICATED_USER = AuthenticatedUserObjectMother.withEmail(EMAIL);

    private static final String TOKEN =
        "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.ANCf_8p1AE4ZQs7QuqGAyyfTEgYrKSjKWkhBk5cIn1_2QVr2jEjmM-1tu7EgnyOf_fAsvdFXva8Sv05iTGzETg";

    @BeforeEach
    public void setup() {
        this.userRepository = mock(UserRepository.class);
        this.phoneRepository = mock(PhoneRepository.class);
        this.jwtUtil = mock(JwtUtil.class);

        this.loginService = new LoginServiceImpl(jwtUtil, userRepository, phoneRepository);

        // Create authentication token
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            AUTHENTICATED_USER, null, Collections.emptyList()
        );

        // Mock the SecurityContext
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        // Set the context into the static holder
        SecurityContextHolder.setContext(context);
    }

    @Test
    public void testLogin_shouldReturnUserInfo() {
        when(jwtUtil.generateToken(Mockito.eq(EMAIL))).thenReturn(TOKEN);
        final UserEntity user = defaultUser(EMAIL);
        when(userRepository.findById(Mockito.eq(AUTHENTICATED_USER.getId()))).thenReturn(Optional.of(user));
        final List<PhoneEntity> userPhones = userPhoneList(user);
        when(phoneRepository.findAllByUser(Mockito.eq(user))).thenReturn(userPhones);

        final ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);

        final UserResponse response = loginService.login();

        verify(userRepository).save(userEntityArgumentCaptor.capture());
        verify(jwtUtil).generateToken(Mockito.eq(EMAIL));

        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getPassword(), response.getPassword());
        assertEquals(userEntityArgumentCaptor.getValue().getLastLogin(), response.getLastLogin());
        assertEquals(TOKEN, response.getToken());
        assertTrue(response.isActive());
        assertEquals(userPhones.size(), response.getPhones().size());
        final Map<Long, PhoneEntity> phoneNumberToPhone = userPhones.stream()
            .collect(Collectors.toMap(PhoneEntity::getNumber, Function.identity()));
        response.getPhones().forEach(phoneDTO -> {
            final PhoneEntity phone = phoneNumberToPhone.get(phoneDTO.getNumber());
            assertNotNull(phone);
            assertEquals(phone.getNumber(), phoneDTO.getNumber());
            assertEquals(phone.getCityCode(), phoneDTO.getCityCode());
            assertEquals(phone.getCountryCode(), phoneDTO.getCountryCode());
        });

    }

    @Test
    public void testLogin_shouldThrowUnauthorizedExceptionWhenContextIsCleared() {
        SecurityContextHolder.clearContext();

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
            loginService.login()
        );
        assertEquals("Unauthorized user", ex.getMessage());
    }

    @Test
    public void testLogin_shouldThrowUnauthorizedExceptionWhenContextDoesNotHaveAuthenticationObject() {

        // Mock the SecurityContext
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
            loginService.login()
        );
        assertEquals("Unauthorized user", ex.getMessage());
    }

    @Test
    public void testLogin_shouldThrowUnauthorizedExceptionWhenPrincipalIsNotAnInstanceOfAuthenticatedUser() {

        // Mock the SecurityContext
        SecurityContext context = mock(SecurityContext.class);
        // Create authentication token
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            EMAIL, null, Collections.emptyList()
        );
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
            loginService.login()
        );
        assertEquals("Unauthorized user", ex.getMessage());
    }

    @Test
    public void testLogin_shouldThrowUnauthorizedExceptionWhenPrincipalIsNull() {

        // Mock the SecurityContext
        SecurityContext context = mock(SecurityContext.class);
        // Create authentication token
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            null, null, Collections.emptyList()
        );
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
            loginService.login()
        );
        assertEquals("Unauthorized user", ex.getMessage());
    }

    @Test
    public void testLogin_shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(Mockito.eq(AUTHENTICATED_USER.getId()))).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
            loginService.login()
        );
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    public void testLogin_shouldThrowUnauthorizedExceptionWhenUserIsNotActive() {
        final UserEntity user = inactiveUser(EMAIL);
        when(userRepository.findById(Mockito.eq(AUTHENTICATED_USER.getId()))).thenReturn(Optional.of(user));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
            loginService.login()
        );
        assertEquals("User is not active", ex.getMessage());
    }

}
