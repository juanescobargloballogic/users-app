package com.users.app.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateTokenAndExtractEmail_shouldGenerateTokenAndExtractEmailSuccessfully() {
        String email = "julio@test.com";

        String token = jwtUtil.generateToken(email);
        String extractedEmail = jwtUtil.extractEmail(token);

        assertThat(token).isNotNull();
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    void testIsTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken("test@mail.com");

        boolean isValid = jwtUtil.isTokenValid(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void testIsTokenValid_shouldReturnFalseForInvalidToken() {
        String invalidToken = "not.a.valid.token";

        boolean isValid = jwtUtil.isTokenValid(invalidToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void testIsTokenValid_shouldThrowExceptionOnTamperedToken() {
        String token = jwtUtil.generateToken("user@mail.com");

        // Tamper the token by changing one character
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtUtil.isTokenValid(tampered)).isFalse();
    }
}
