package com.example.demolink.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret",
                "my-super-secret-key-for-jwt-authentication-123456");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 100000L);
    }

    @Test
    void shouldGenerateAndParseToken() {

        Authentication auth = mock(Authentication.class);
        UserDetailsImpl user = mock(UserDetailsImpl.class);

        when(auth.getPrincipal()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("john");

        String token = jwtService.generateToken(auth);

        assertNotNull(token);
        assertEquals("john", jwtService.extractUsername(token));
    }
}
