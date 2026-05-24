package com.example.demolink.service;

import com.example.demolink.model.dto.request.LoginRequest;
import com.example.demolink.model.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);
}
