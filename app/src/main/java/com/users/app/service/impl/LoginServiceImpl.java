package com.users.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.users.app.dto.PhoneDTO;
import com.users.app.dto.UserResponse;
import com.users.app.entity.UserEntity;
import com.users.app.exception.NotFoundException;
import com.users.app.exception.UnauthorizedException;
import com.users.app.repository.PhoneRepository;
import com.users.app.repository.UserRepository;
import com.users.app.service.LoginService;
import com.users.app.transformer.PhoneTransformer;
import com.users.app.transformer.UserTransformer;
import com.users.app.util.JwtUtil;

@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    private final PhoneRepository phoneRepository;

    public LoginServiceImpl(JwtUtil jwtUtil, UserRepository userRepository, PhoneRepository phoneRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.phoneRepository = phoneRepository;
    }

    @Override
    public UserResponse login(final String auth) {
        String token = auth.replace("Bearer ", "");

        if (!jwtUtil.isTokenValid(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);
        logger.info("Extracted email: {}", email);

        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.isActive()) {
            throw new UnauthorizedException("User is not active");
        }

        // Update lastLogin and regenerate token
        user.setLastLogin(LocalDateTime.now());
        String newToken = jwtUtil.generateToken(user.getEmail());
        userRepository.save(user);
        List<PhoneDTO> userPhones = PhoneTransformer.toDTOList(phoneRepository.findAllByUser(user));

        return UserTransformer.toUserResponse(user, userPhones, newToken);
    }



}
