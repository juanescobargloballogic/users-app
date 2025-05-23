package com.users.app.service;

import com.users.app.dto.UserRequest;
import com.users.app.dto.UserResponse;

public interface UserService {

    UserResponse createUser(UserRequest userRequest);

}
