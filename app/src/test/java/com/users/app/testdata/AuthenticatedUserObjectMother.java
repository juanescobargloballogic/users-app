package com.users.app.testdata;

import java.util.UUID;

import com.users.app.dto.AuthenticatedUser;

public class AuthenticatedUserObjectMother {

    public static AuthenticatedUser withEmail(final String email) {
        return new AuthenticatedUser(UUID.randomUUID(), email, "julio");
    }

}
