package com.example.demolink.model.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkResponse {

    private Long id;
    private String originalLink;
    private String shortLink;
    private String shortUrl;
    private Long linkFollows;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean active;
    private UserResponse user;
}
