package com.example.demolink.controller;

import com.example.demolink.model.dto.request.LoginRequest;
import com.example.demolink.model.dto.request.RegisterRequest;
import com.example.demolink.model.dto.response.AuthResponse;
import com.example.demolink.service.AuthService;
import com.example.demolink.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/V1/auth")
public class AuthControllerV1 {

    private final AuthService authService;
    private final UserService userService;

    public AuthControllerV1(AuthService authService,
                            UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    @ApiResponse(responseCode = "201", description = "Account created.")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest request) {

        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

}
