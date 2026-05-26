package com.example.demolink.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.example.demolink.security.config.AuthEntryPointJwt;
import com.example.demolink.security.config.SecurityConfig;
import com.example.demolink.security.filter.JwtFilter;
import com.example.demolink.security.service.JwtService;
import com.example.demolink.security.service.UserDetailsServiceImpl;
import com.example.demolink.service.LinkService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RedirectControllerV1.class)
@Import(SecurityConfig.class)
class RedirectControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LinkService linkService;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;
    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @MockitoBean
    private JwtFilter jwtFilter;
    @MockitoBean
    private JwtService jwtService;

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
    void shouldRedirectToOriginalUrl() throws Exception {

        when(linkService.getOriginalLink("shortLink")).thenReturn("https://github.com");

        mockMvc.perform(get("/{shortLink}", "shortLink"))
                .andExpect(status().isFound()) // Перевіряємо статус 302 Found
                .andExpect(header().string("Location", "https://github.com"));
    }
}
