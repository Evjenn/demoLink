package com.example.demolink.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demolink.exception.BaseException;
import com.example.demolink.model.dto.request.RegisterRequest;
import com.example.demolink.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("password123");

        when(userRepository.existsByUsername("john")).thenReturn(true);

        BaseException exception = assertThrows(BaseException.class, () -> {
            userService.register(request);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Username 'john' is already taken", exception.getMessage());

        verify(userRepository, never()).save(any());
    }
}
