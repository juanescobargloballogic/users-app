package com.users.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.users.app.dto.AuthenticatedUser;
import com.users.app.dto.PhoneDTO;
import com.users.app.dto.UserResponse;
import com.users.app.entity.UserEntity;
import com.users.app.exception.NotFoundException;
import com.users.app.exception.UnauthorizedException;
import com.users.app.util.JwtUtil;
import com.users.app.repository.PhoneRepository;
import com.users.app.repository.UserRepository;
import com.users.app.service.LoginService;
import com.users.app.transformer.PhoneTransformer;
import com.users.app.transformer.UserTransformer;

/**
 * Implements the login process using Spring Security context.
 * Assumes the user has already been authenticated by the {@link com.users.app.filter.JwtAuthenticationFilter} security filter.
 */
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

    /**
     * Retrieves the currently authenticated user from the SecurityContext,
     * updates their last login timestamp, generates a new JWT token,
     * and returns the user's information.
     *
     * @return UserResponse containing user details and a new JWT token
     * @throws UnauthorizedException if no authenticated user is present
     */
    @Override
    public UserResponse login() {

        AuthenticatedUser authenticatedUser = Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(a -> a.getPrincipal() != null && a.getPrincipal() instanceof AuthenticatedUser)
            .map(a -> (AuthenticatedUser) a.getPrincipal())
            .orElseThrow(() -> new UnauthorizedException("Unauthorized user"));

        logger.info("Authenticated user: {}", authenticatedUser);

        UserEntity user = userRepository.findById(authenticatedUser.getId())
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
