package com.example.demolink.service.generator;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class ShortLinkGenerator {

    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 8;
    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            builder.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return builder.toString();
    }
}
