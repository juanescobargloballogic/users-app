package com.users.app.testdata;

import java.time.LocalDateTime;
import java.util.UUID;

import com.users.app.entity.UserEntity;

public class UserEntityObjectMother {

    public static UserEntity defaultUser(final String email) {
        final UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setName("Name");
        user.setActive(true);
        user.setCreated(LocalDateTime.now().minusDays(10));
        user.setLastLogin(LocalDateTime.now().minusDays(1));
        user.setPassword("Pass");

        return user;
    }

    public static UserEntity inactiveUser(final String email) {
        UserEntity user = defaultUser(email);
        user.setActive(false);
        return user;
    }

    public static UserEntity withValues(UUID id, String name, String email, String password,
        LocalDateTime created, LocalDateTime lastLogin, boolean active) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setCreated(created);
        user.setLastLogin(lastLogin);
        user.setActive(active);
        return user;
    }

}
