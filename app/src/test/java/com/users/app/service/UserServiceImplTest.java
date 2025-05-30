package com.users.app.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;

import com.users.app.dto.PhoneDTO;
import com.users.app.dto.UserRequest;
import com.users.app.dto.UserResponse;
import com.users.app.entity.PhoneEntity;
import com.users.app.entity.UserEntity;
import com.users.app.exception.UserAlreadyExistsException;
import com.users.app.util.JwtUtil;
import com.users.app.repository.PhoneRepository;
import com.users.app.repository.UserRepository;
import com.users.app.service.impl.UserServiceImpl;
import com.users.app.testdata.UserEntityObjectMother;
import com.users.app.transformer.UserTransformer;

import static java.util.function.Function.*;

import static org.junit.jupiter.api.Assertions.*;

import static com.users.app.testdata.PhoneDTOObjectMother.userPhoneList;
import static com.users.app.testdata.UserRequestObjectMother.withEmailAndPhones;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceImplTest {

    private UserRepository userRepository;

    private PhoneRepository phoneRepository;

    private JwtUtil jwtUtil;

    private UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String EMAIL = "name@email.com";

    private static final String TOKEN =
        "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.ANCf_8p1AE4ZQs7QuqGAyyfTEgYrKSjKWkhBk5cIn1_2QVr2jEjmM-1tu7EgnyOf_fAsvdFXva8Sv05iTGzETg";

    @BeforeEach
    public void setup() {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.phoneRepository = Mockito.mock(PhoneRepository.class);
        this.jwtUtil = Mockito.mock(JwtUtil.class);

        this.userService = new UserServiceImpl(userRepository, phoneRepository, jwtUtil);
    }

    @Test
    public void testCreateUser_withPhoneList() {
        final List<PhoneDTO> userPhones = userPhoneList();
        testCreateUserBasedOnPhoneList(userPhones);
    }

    @Test
    public void testCreateUser_withEmptyPhoneList() {
        testCreateUserBasedOnPhoneList(Collections.emptyList());
    }

    @Test
    public void testCreateUser_withNullPhoneList() {
        testCreateUserBasedOnPhoneList(null);
    }

    private void testCreateUserBasedOnPhoneList(List<PhoneDTO> userPhones) {
        final UserRequest userRequest = withEmailAndPhones(EMAIL, userPhones);
        Mockito.when(userRepository.findByEmail(Mockito.eq(EMAIL))).thenReturn(Optional.empty());
        Mockito.when(jwtUtil.generateToken(Mockito.eq(EMAIL))).thenReturn(TOKEN);
        final UserEntity savedUser = UserTransformer.toEntity(userRequest, null, true);
        savedUser.setId(UUID.randomUUID());
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(userRequest);

        final ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.verify(userRepository).save(userEntityArgumentCaptor.capture());

        if (!CollectionUtils.isEmpty(userPhones)) {
            @SuppressWarnings("unchecked") final ArgumentCaptor<List<PhoneEntity>> phoneEntityArgumentCaptor =
                ArgumentCaptor.forClass(List.class);
            Mockito.verify(phoneRepository).saveAll(phoneEntityArgumentCaptor.capture());

            assertThat(phoneEntityArgumentCaptor.getValue()).hasSize(userPhones.size());
            assertSavedPhones(userPhones, phoneEntityArgumentCaptor, savedUser);
        }
        assertNotEquals(userRequest.getPassword(), userEntityArgumentCaptor.getValue().getPassword());
        assertTrue(passwordEncoder.matches(userRequest.getPassword(), userEntityArgumentCaptor.getValue().getPassword()));

        Mockito.verify(jwtUtil).generateToken(Mockito.eq(EMAIL));
        assertEquals(savedUser.getId(), response.getId());
        assertEquals(savedUser.getCreated(), response.getCreated());
        assertEquals(savedUser.getLastLogin(), response.getLastLogin());
        assertEquals(TOKEN, response.getToken());
        assertEquals(savedUser.isActive(), response.isActive());
    }

    private static void assertSavedPhones(List<PhoneDTO> userPhones, ArgumentCaptor<List<PhoneEntity>> phoneEntityArgumentCaptor,
        UserEntity savedUser) {
        final Map<Long, PhoneDTO> phoneNumberToPhoneMap = userPhones.stream()
            .collect(Collectors.toMap(PhoneDTO::getNumber, identity()));
        phoneEntityArgumentCaptor.getValue().forEach(phoneEntity -> {
            assertNotNull(phoneEntity.getUser());
            assertNotNull(phoneEntity.getUser().getId());
            assertEquals(savedUser.getId(), phoneEntity.getUser().getId());
            final PhoneDTO expectedPhone = phoneNumberToPhoneMap.get(phoneEntity.getNumber());
            assertNotNull(expectedPhone);
            assertEquals(expectedPhone.getNumber(), phoneEntity.getNumber());
            assertEquals(expectedPhone.getCountryCode(), phoneEntity.getCountryCode());
            assertEquals(expectedPhone.getCityCode(), phoneEntity.getCityCode());
        });
    }

    @Test
    public void testCreateUser_shouldThrowUserAlreadyExistsExceptionWhenUserAlreadyExists() {
        final UserRequest userRequest = withEmailAndPhones(EMAIL, null);
        Mockito.when(userRepository.findByEmail(Mockito.eq(EMAIL))).thenReturn(Optional.of(UserEntityObjectMother.defaultUser(EMAIL)));

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
            () -> userService.createUser(userRequest)
        );
        assertEquals(String.format("A user with email %s already exists", EMAIL), ex.getMessage());
    }

}
