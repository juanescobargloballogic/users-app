package com.users.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.users.app.filter.JwtAuthenticationFilter;
import com.users.app.util.JwtUtil;
import com.users.app.repository.UserRepository;

/**
 * Configures Spring Security for the application.
 * - Disables CSRF (appropriate for stateless REST APIs)
 * - Allows unauthenticated access to sign-up and Swagger-related endpoints
 * - Requires authentication for all other endpoints
 * - Adds a custom JWT authentication filter before the standard UsernamePasswordAuthenticationFilter
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public SecurityConfig(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    /**
     * Configures the HTTP security rules for the application.
     * - Disables CSRF protection (suitable for APIs that don’t use browser-based sessions)
     * - Permits unauthenticated access to:
     * • /users/sign-up (user registration)
     * • /v3/api-docs/** and /swagger-ui.html/** (Swagger docs)
     * - Secures all other endpoints by requiring authentication
     * - Registers the JwtAuthenticationFilter to validate and process JWT tokens
     *
     * @param http the HttpSecurity object used to configure web security
     * @throws Exception if an error occurs during configuration
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Stateless API; CSRF not needed
            .authorizeRequests()
            .antMatchers("/users/sign-up", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
            .permitAll() // Public endpoints
            .anyRequest().authenticated() // Secure everything else
            .and()
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class
            ); // Add JWT filter before standard auth
        ;
    }
}
