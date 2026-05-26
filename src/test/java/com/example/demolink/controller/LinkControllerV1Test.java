package com.example.demolink.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demolink.exception.NotFoundException;
import com.example.demolink.mapper.LinkMapper;
import com.example.demolink.model.dto.response.LinkResponse;
import com.example.demolink.model.entity.LinkEntity;
import com.example.demolink.security.filter.JwtFilter;
import com.example.demolink.security.service.JwtService;
import com.example.demolink.security.service.UserDetailsImpl;
import com.example.demolink.service.LinkService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LinkControllerV1.class)
class LinkControllerV1Test {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private JwtFilter jwtFilter;
    @MockitoBean
    private LinkService linkService;
    @MockitoBean
    private LinkMapper linkMapper;
    @MockitoBean
    private UserDetailsService userDetailsService;

    private UserDetailsImpl principalUser;

    @BeforeEach
    void setUserForTest() throws Exception {

        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(request, response);
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());

        principalUser = new UserDetailsImpl(
                2L,
                "John",
                "passworde",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void shouldCreateLink() throws Exception {

        LinkEntity entity = new LinkEntity();
        entity.setId(1L);

        LinkResponse response = new LinkResponse();
        response.setId(1L);

        when(linkService.create(any(), anyLong())).thenReturn(entity);
        when(linkMapper.toResponse(entity)).thenReturn(response);

        mockMvc.perform(post("/api/V1/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalLink\":\"https://github.com\"}")
                    .with(user(principalUser))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn404WhenLinkNotFound() throws Exception {

        when(linkService.create(any(), anyLong())).thenThrow(new NotFoundException("Link not found"));

        mockMvc.perform(post("/api/V1/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalLink\":\"https://github.com\"}")
                        .with(user(principalUser))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenOriginalLinkIsEmpty() throws Exception {

        mockMvc.perform(post("/api/V1/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalLink\":\"\"}")
                        .with(user(principalUser))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
