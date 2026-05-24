package com.example.demolink.model.dto.request;

import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLinkRequest {

    @Future
    private LocalDateTime expiresAt;

    private boolean active;

}
