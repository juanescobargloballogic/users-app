package com.users.app.transformer;

import com.users.app.dto.AuthenticatedUser;
import com.users.app.dto.UserRequest;
import com.users.app.dto.UserResponse;
import com.users.app.entity.UserEntity;
import com.users.app.dto.PhoneDTO;
import com.users.app.testdata.PhoneDTOObjectMother;
import com.users.app.testdata.UserEntityObjectMother;
import com.users.app.testdata.UserRequestObjectMother;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTransformerTest {

    @Test
    void testToEntity_shouldTransformUserRequestToUserEntity() {
        UserRequest request = UserRequestObjectMother.defaultUserRequest();
        String encodedPassword = "encodedPassword123";
        boolean isActive = true;

        UserEntity result = UserTransformer.toEntity(request, encodedPassword, isActive);

        assertThat(result.getName()).isEqualTo(request.getName());
        assertThat(result.getEmail()).isEqualTo(request.getEmail());
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getCreated()).isNotNull();
        assertThat(result.getLastLogin()).isNotNull();
    }

    @Test
    void testToUserResponse_shouldTransformUserEntityToUserResponse() {
        UserEntity user = UserEntityObjectMother.withValues(
            UUID.randomUUID(),
            "Julio",
            "julio@test.com",
            "encodedPassword123",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now(),
            true
        );
        List<PhoneDTO> phones = List.of(PhoneDTOObjectMother.defaultPhone());
        String token = "mock.jwt.token";

        UserResponse response = UserTransformer.toUserResponse(user, phones, token);

        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getName()).isEqualTo(user.getName());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getCreated()).isEqualTo(user.getCreated());
        assertThat(response.getLastLogin()).isEqualTo(user.getLastLogin());
        assertThat(response.getToken()).isEqualTo(token);
        assertThat(response.getPassword()).isEqualTo(user.getPassword());
        assertThat(response.isActive()).isEqualTo(user.isActive());
        assertThat(response.getPhones()).isEqualTo(phones);
    }

    @Test
    void testToAuthenticatedUser_shouldTransformUserEntityToAuthenticatedUser() {
        UserEntity user = UserEntityObjectMother.withValues(
            UUID.randomUUID(),
            "Julio",
            "julio@test.com",
            "encodedPassword123",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now(),
            true
        );

        AuthenticatedUser authenticatedUser = UserTransformer.toAuthenticatedUser(user);

        assertThat(authenticatedUser.getId()).isEqualTo(user.getId());
        assertThat(authenticatedUser.getName()).isEqualTo(user.getName());
        assertThat(authenticatedUser.getEmail()).isEqualTo(user.getEmail());
    }

}

