package com.example.demolink.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.example.demolink.model.dto.response.AuthResponse;
import com.example.demolink.security.config.AuthEntryPointJwt;
import com.example.demolink.security.config.SecurityConfig;
import com.example.demolink.security.filter.JwtFilter;
import com.example.demolink.security.service.JwtService;
import com.example.demolink.security.service.UserDetailsServiceImpl;
import com.example.demolink.service.AuthService;
import com.example.demolink.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthControllerV1.class)
@Import(SecurityConfig.class)
class AuthControllerV1Test {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;
    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private JwtFilter jwtFilter;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {

        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(request, response);
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {

        mockMvc.perform(post("/api/V1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "username": "newuser",
                        "password": "securePassword123"
                    }
                    """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenRegisterValidationFails() throws Exception {

        mockMvc.perform(post("/api/V1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "username": "",
                        "password": ""
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Given
        when(authService.login(any())).thenReturn(new AuthResponse("mocked-jwt-token"));

        // When & Then
        mockMvc.perform(post("/api/V1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"password\":\"password\"}")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenLoginValidationFails() throws Exception {

        mockMvc.perform(post("/api/V1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"password\":\"\"}")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenBadCredentials() throws Exception {

        when(authService.login(any())).thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/V1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"password\":\"wrong_password\"}")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}

