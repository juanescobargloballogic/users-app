package com.users.app.transformer;

import java.time.LocalDateTime;
import java.util.List;

import com.users.app.dto.PhoneDTO;
import com.users.app.dto.UserRequest;
import com.users.app.dto.UserResponse;
import com.users.app.entity.UserEntity;

public final class UserTransformer {

    private UserTransformer() {
        // Private constructor to prevent instantiation
    }

    public static UserEntity toEntity(UserRequest userRequest, String password, boolean isActive) {
        UserEntity user = new UserEntity();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(password);
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(isActive);

        return user;

    }

    public static UserResponse toUserResponse(final UserEntity user, final List<PhoneDTO> userPhones, final String token) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setCreated(user.getCreated());
        response.setLastLogin(user.getLastLogin());
        response.setToken(token);
        response.setActive(user.isActive());
        response.setPhones(userPhones);
        response.setPassword(user.getPassword());
        return response;
    }

}
