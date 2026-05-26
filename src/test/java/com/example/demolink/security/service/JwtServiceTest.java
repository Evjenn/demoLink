package com.example.demolink.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
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

    @Test
    void shouldValidateTokenSuccessfully_whenTokenIsNotExpired() {

        Authentication auth = mock(Authentication.class);
        UserDetailsImpl user = mock(UserDetailsImpl.class);

        when(auth.getPrincipal()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("john");
        when(user.getAuthorities()).thenReturn(Collections.emptyList());

        String token = jwtService.generateToken(auth);
        boolean isValid = jwtService.isValid(token);
        assertTrue(isValid);
    }

    @Test
    void shouldReturnFalse_whenTokenIsExpired() throws InterruptedException {

        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);
        Authentication auth = mock(Authentication.class);
        UserDetailsImpl user = mock(UserDetailsImpl.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("john");
        when(user.getAuthorities()).thenReturn(Collections.emptyList());

        String token = jwtService.generateToken(auth);
        Thread.sleep(5);
        try {
            boolean isValid = jwtService.isValid(token);
            assertFalse(isValid);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}
