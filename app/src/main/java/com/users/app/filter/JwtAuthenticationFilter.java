package com.users.app.filter;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.users.app.util.JwtUtil;
import com.users.app.repository.UserRepository;
import com.users.app.transformer.UserTransformer;

/**
 * A custom authentication filter that processes incoming requests to extract and validate JWT tokens.
 * This filter:
 * - Extracts the token from the "Authorization" header (expects "Bearer <token>")
 * - Validates the token using the JwtUtil component
 * - Retrieves the user from the database using the extracted email
 * - Builds an AuthenticatedUser and sets it into the SecurityContext
 * The filter runs once per request and ensures authentication for protected endpoints.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    /**
     * Intercepts incoming HTTP requests to extract and validate JWT tokens.
     * If the token is valid, the associated user is retrieved and authenticated
     * in the Spring Security context.
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain to pass the request along
     * @throws ServletException if the request processing fails
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                String email = jwtUtil.extractEmail(token);

                userRepository.findByEmail(email)
                    .map(UserTransformer::toAuthenticatedUser)
                    .ifPresent(authenticatedUser -> {
                        final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            authenticatedUser,
                            null,
                            Collections.emptyList()
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });
            }
        }

        filterChain.doFilter(request, response);
    }
}
