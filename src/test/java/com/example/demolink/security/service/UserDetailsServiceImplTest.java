package com.example.demolink.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.demolink.model.entity.Role;
import com.example.demolink.model.entity.UserEntity;
import com.example.demolink.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldLoadUserByUsernameWhenUserExists() {
        // 1. Тестуємо успішний сценарій пошуку користувача
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("john");
        userEntity.setPassword("encoded_password");
        userEntity.setRole(Role.USER); // Вкажіть вашу дефолтну роль

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(userEntity));

        UserDetails result = userDetailsService.loadUserByUsername("john");

        assertNotNull(result);
        assertEquals("john", result.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        // 2. Тестуємо бранч помилки (гілка else), що дасть найбільше відсотків!
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknown");
        });
    }
}
