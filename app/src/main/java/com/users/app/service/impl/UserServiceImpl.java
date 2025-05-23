package com.users.app.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.users.app.dto.UserRequest;
import com.users.app.dto.UserResponse;
import com.users.app.entity.UserEntity;
import com.users.app.exception.UserAlreadyExistsException;
import com.users.app.repository.PhoneRepository;
import com.users.app.repository.UserRepository;
import com.users.app.service.UserService;
import com.users.app.transformer.PhoneTransformer;
import com.users.app.transformer.UserTransformer;
import com.users.app.util.JwtUtil;

import io.jsonwebtoken.lang.Collections;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PhoneRepository phoneRepository;

    private final JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PhoneRepository phoneRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.phoneRepository = phoneRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        userRepository.findByEmail(userRequest.getEmail()).ifPresent(u -> {
            throw new UserAlreadyExistsException(String.format("A user with email %s already exists", userRequest.getEmail()));
        });
        UserEntity createdUser = userRepository.save(UserTransformer.toEntity(userRequest,
            passwordEncoder.encode(userRequest.getPassword()), true));
        if (!Collections.isEmpty(userRequest.getPhones())) {
            phoneRepository.saveAll(PhoneTransformer.toEntityList(userRequest.getPhones(), createdUser));
        }
        String token = jwtUtil.generateToken(createdUser.getEmail());

        return new UserResponse(createdUser.getId(), createdUser.getCreated(), createdUser.getLastLogin(), token, createdUser.isActive());
    }


}

