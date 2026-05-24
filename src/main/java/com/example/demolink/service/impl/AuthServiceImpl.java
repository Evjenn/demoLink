package com.example.demolink.service.impl;

import com.example.demolink.model.dto.request.LoginRequest;
import com.example.demolink.model.dto.response.AuthResponse;
import com.example.demolink.security.service.JwtService;
import com.example.demolink.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );
        String token = jwtService.generateToken(authentication);
        return new AuthResponse(token);
    }

}
