package com.example.demolink.security.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demolink.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtFilter jwtFilter;

    @Test
    void shouldSkipFilterWhenNoAuthorizationHeader() throws Exception {

        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldExtractTokenAndAuthenticateWhenTokenIsValid() throws Exception {

        String mockToken = "valid.jwt.token";
        String username = "john_doe";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + mockToken);
        when(jwtService.extractUsername(mockToken)).thenReturn(username);
        when(jwtService.isValid(mockToken)).thenReturn(true);

        UserDetails mockDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(mockDetails);
        jwtFilter.doFilter(request, response, filterChain);

        verify(jwtService, times(1)).extractUsername(mockToken);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid() throws Exception {

        String mockToken = "expired.jwt.token";
        String username = "john_doe";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + mockToken);
        when(jwtService.extractUsername(mockToken)).thenReturn(username);
        when(jwtService.isValid(mockToken)).thenReturn(false);

        jwtFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldSkipFilterWhenHeaderDoesNotStartWithBearer() throws Exception {

        when(request.getHeader("Authorization")).thenReturn("Basic c29tZXRva2Vu");

        jwtFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }
}
