package com.users.app.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.users.app.dto.UserResponse;
import com.users.app.entity.PhoneEntity;
import com.users.app.entity.UserEntity;
import com.users.app.exception.NotFoundException;
import com.users.app.exception.UnauthorizedException;
import com.users.app.repository.PhoneRepository;
import com.users.app.repository.UserRepository;
import com.users.app.service.impl.LoginServiceImpl;
import com.users.app.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static com.users.app.testdata.PhoneEntityObjectMother.*;
import static com.users.app.testdata.UserEntityObjectMother.defaultUser;
import static com.users.app.testdata.UserEntityObjectMother.inactiveUser;

public class LoginServiceImplTest {

    private LoginService loginService;

    private UserRepository userRepository;

    private PhoneRepository phoneRepository;

    private JwtUtil jwtUtil;

    private static final String EMAIL = "name@email.com";

    private static final String TOKEN =
        "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.ANCf_8p1AE4ZQs7QuqGAyyfTEgYrKSjKWkhBk5cIn1_2QVr2jEjmM-1tu7EgnyOf_fAsvdFXva8Sv05iTGzETg";

    private static final String AUTH_HEADER =
        "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqdWFuLnBlcmV6QGV4YW1wbGUiLCJpYXQiOjE3NDc4ODM0MDMsImV4cCI6MTc0Nzk2OTgwM30.6BkXA50UOTip9nFd_UMKqmYGcuFX2F0cUTDNAQmnJU90_ocum3BU_7CV0pVYHsB-Am1zd1PHidKy9Vz-h7ZgsQ";

    @BeforeEach
    public void setup() {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.phoneRepository = Mockito.mock(PhoneRepository.class);
        this.jwtUtil = Mockito.mock(JwtUtil.class);

        this.loginService = new LoginServiceImpl(jwtUtil, userRepository, phoneRepository);
    }

    @Test
    public void testLogin_shouldReturnUserInfo() {
        Mockito.when(jwtUtil.isTokenValid(Mockito.anyString())).thenReturn(true);
        Mockito.when(jwtUtil.extractEmail(Mockito.anyString())).thenReturn(EMAIL);
        Mockito.when(jwtUtil.generateToken(Mockito.eq(EMAIL))).thenReturn(TOKEN);
        final UserEntity user = defaultUser(EMAIL);
        Mockito.when(userRepository.findByEmail(Mockito.eq(EMAIL))).thenReturn(Optional.of(user));
        final List<PhoneEntity> userPhones = userPhoneList(user);
        Mockito.when(phoneRepository.findAllByUser(Mockito.eq(user))).thenReturn(userPhones);

        final ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);

        final UserResponse response = loginService.login(AUTH_HEADER);

        Mockito.verify(userRepository).save(userEntityArgumentCaptor.capture());

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
    public void testLogin_shouldThrowUnauthorizedExceptionWhenTokenIsNotValid() {
        Mockito.when(jwtUtil.isTokenValid(Mockito.anyString())).thenReturn(false);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
            loginService.login(AUTH_HEADER)
        );
        assertEquals("Invalid or expired token", ex.getMessage());
    }

    @Test
    public void testLogin_shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        Mockito.when(jwtUtil.isTokenValid(Mockito.anyString())).thenReturn(true);
        Mockito.when(jwtUtil.extractEmail(Mockito.anyString())).thenReturn(EMAIL);
        Mockito.when(userRepository.findByEmail(Mockito.eq(EMAIL))).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
            loginService.login(AUTH_HEADER)
        );
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    public void testLogin_shouldThrowUnauthorizedExceptionWhenUserIsNotActive() {
        Mockito.when(jwtUtil.isTokenValid(Mockito.anyString())).thenReturn(true);
        Mockito.when(jwtUtil.extractEmail(Mockito.anyString())).thenReturn(EMAIL);
        final UserEntity user = inactiveUser(EMAIL);
        Mockito.when(userRepository.findByEmail(Mockito.eq(EMAIL))).thenReturn(Optional.of(user));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
            loginService.login(AUTH_HEADER)
        );
        assertEquals("User is not active", ex.getMessage());
    }

}
