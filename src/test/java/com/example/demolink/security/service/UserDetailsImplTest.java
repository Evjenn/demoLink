package com.example.demolink.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class UserDetailsImplTest {

    @Test
    void shouldReturnDefaultSecurityProperties() {

        UserDetailsImpl user = new UserDetailsImpl(
                1L,
                "john",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        assertEquals(1L, user.getId());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
}
