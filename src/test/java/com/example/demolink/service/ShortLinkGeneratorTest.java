package com.example.demolink.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.demolink.service.generator.ShortLinkGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShortLinkGeneratorTest {

    private ShortLinkGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ShortLinkGenerator();
    }

    @Test
    void shouldGenerateCorrectLength() {

        String result = generator.generate();
        assertThat(result)
                .isNotNull()
                .hasSize(8);
    }

    @Test
    void shouldContainOnlyAllowedCharacters() {

        String allowedCharsRegex = "^[a-zA-Z0-9]+$";
        String result = generator.generate();
        assertThat(result).matches(allowedCharsRegex);
    }

    @Test
    void shouldGenerateUniqueStrings() {

        String first = generator.generate();
        String second = generator.generate();
        assertThat(first).isNotEqualTo(second);
    }
}
