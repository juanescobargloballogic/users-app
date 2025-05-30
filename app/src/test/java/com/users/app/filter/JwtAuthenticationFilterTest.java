package com.users.app.filter;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.users.app.dto.AuthenticatedUser;
import com.users.app.entity.UserEntity;
import com.users.app.repository.UserRepository;
import com.users.app.testdata.UserEntityObjectMother;
import com.users.app.util.JwtUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;

    private JwtUtil jwtUtil;

    private UserRepository userRepository;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        userRepository = mock(UserRepository.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        filter = new JwtAuthenticationFilter(jwtUtil, userRepository);
    }

    @Test
    void testDoFilter_shouldSetAuthenticationWhenTokenIsValid() throws Exception {
        String token = "valid-token";
        String email = "user@example.com";
        UserEntity user = UserEntityObjectMother.defaultUser(email);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(user));

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertThat(auth).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(auth.getPrincipal()).isInstanceOf(AuthenticatedUser.class);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) auth.getPrincipal();
        assertEquals(email, authenticatedUser.getEmail());
        assertEquals(user.getId(), authenticatedUser.getId());
        assertEquals(user.getName(), authenticatedUser.getName());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilter_shouldSkipAuthenticationWhenTokenIsInvalid() throws Exception {
        String token = "invalid-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.isTokenValid(token)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilter_shouldSkipAuthenticationWhenTokenIsNotBearer() throws Exception {
        String token = "invalid-token";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.isTokenValid(token)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, times(0)).isTokenValid(any());
    }

    @Test
    void testDoFilter_shouldSkipAuthenticationWhenMissingHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}

