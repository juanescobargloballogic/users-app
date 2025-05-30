package com.users.app.dto;

import java.util.UUID;

public class AuthenticatedUser {

    private final UUID id;

    private final String email;

    private final String name;

    public AuthenticatedUser(UUID id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}

