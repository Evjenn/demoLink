package com.example.demolink.exception;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Map<String, String> fields;
    private String path;
}
