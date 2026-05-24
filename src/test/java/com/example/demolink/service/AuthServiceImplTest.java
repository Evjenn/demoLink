package com.example.demolink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demolink.model.dto.request.LoginRequest;
import com.example.demolink.model.dto.response.AuthResponse;
import com.example.demolink.security.service.JwtService;
import com.example.demolink.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldReturnToken_whenLoginSuccess() {

        // given
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("1234");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtService.generateToken(authentication))
                .thenReturn("fake-jwt");

        // when
        AuthResponse response = authService.login(request);

        // then
        assertEquals("fake-jwt", response.getToken());
    }
}
