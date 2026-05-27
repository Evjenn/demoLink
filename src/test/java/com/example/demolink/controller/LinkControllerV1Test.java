package com.example.demolink.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demolink.exception.BaseException;
import com.example.demolink.mapper.LinkMapper;
import com.example.demolink.model.dto.response.LinkResponse;
import com.example.demolink.model.dto.response.UserResponse;
import com.example.demolink.model.entity.LinkEntity;
import com.example.demolink.security.filter.JwtFilter;
import com.example.demolink.security.service.JwtService;
import com.example.demolink.security.service.UserDetailsImpl;
import com.example.demolink.service.LinkService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
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
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void shouldCreateLink() throws Exception {
        LinkEntity entity = new LinkEntity();
        entity.setId(1L);

        LinkResponse response = new LinkResponse();
        response.setId(1L);
        response.setOriginalLink("https://github.com");
        response.setShortLink("short123");
        response.setShortUrl("http://localhost:8080/short123");
        response.setLinkFollows(0L);
        response.setCreatedAt(LocalDateTime.now());
        response.setExpiresAt(LocalDateTime.now().plusDays(10));
        response.setActive(true);

        UserResponse userDto = new UserResponse();
        userDto.setId(principalUser.getId());
        userDto.setUsername(principalUser.getUsername());
        response.setUser(userDto);

        when(linkService.create(any(), anyLong())).thenReturn(entity);
        when(linkMapper.toResponse(entity)).thenReturn(response);

        mockMvc.perform(post("/api/V1/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalLink\":\"https://github.com\"}")
                        .with(user(principalUser))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.user.id").value(2))
                .andExpect(jsonPath("$.user.username").value("John"));
    }

    @Test
    void shouldReturn404WhenLinkNotFound() throws Exception {

        when(linkService.create(any(), anyLong())).thenThrow(new BaseException("Link not found",
                HttpStatus.NOT_FOUND));
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

    @Test
    void shouldReturnUserActiveLinksSuccessfully() throws Exception {
        LinkEntity entity = new LinkEntity();
        entity.setId(1L);

        LinkResponse response = new LinkResponse();
        response.setId(1L);

        UserResponse userDto = new UserResponse();
        userDto.setId(principalUser.getId());
        userDto.setUsername(principalUser.getUsername());
        response.setUser(userDto);

        when(linkService.getUserActiveLinks(anyLong())).thenReturn(List.of(entity));
        when(linkMapper.toResponse(entity)).thenReturn(response);

        mockMvc.perform(get("/api/V1/links/active")
                        .with(user(principalUser))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].user.username").value("John"));
    }

    @Test
    void shouldReturn403WhenUserIsNotOwner() throws Exception {

        doThrow(new BaseException("Forbidden", HttpStatus.FORBIDDEN))
                .when(linkService).deleteById(anyLong(), anyLong());

        mockMvc.perform(delete("/api/V1/links/1")
                        .with(user(principalUser))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
