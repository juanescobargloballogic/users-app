package com.users.app.testdata;

import java.util.List;

import com.users.app.dto.PhoneDTO;
import com.users.app.dto.UserRequest;

public class UserRequestObjectMother {

    public static UserRequest withEmailAndPhones(final String email, final List<PhoneDTO> userPhones) {
        final UserRequest user = new UserRequest();
        user.setName("Julio");
        user.setEmail(email);
        user.setPassword("a2asfGfdfdf4");
        user.setPhones(userPhones);

        return user;
    }

    public static UserRequest defaultUserRequest() {
        return withEmailAndPhones("julio@test.com", null);
    }



}
