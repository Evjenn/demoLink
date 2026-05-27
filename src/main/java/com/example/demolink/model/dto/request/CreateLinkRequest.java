package com.example.demolink.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class CreateLinkRequest {

    @NotBlank
    @URL(message = "Invalid URL format")
    @Size(max = 2048)
    private String originalLink;

    @Future
    private LocalDateTime expiresAt;
}
