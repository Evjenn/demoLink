package com.example.demolink.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.demolink.service.generator.ShortLinkGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShortLinkGeneratorTest {

    private ShortLinkGenerator generator;

    @BeforeEach
    void setUp() {
        // Просто створюємо екземпляр класу через new, без допомоги Spring
        generator = new ShortLinkGenerator();
    }

    @Test
    void shouldGenerateCorrectLength() {
        // When
        String result = generator.generate();
        // Then
        assertThat(result)
                .isNotNull()
                .hasSize(8); // Перевіряємо, що довжина строго 8 символів
    }

    @Test
    void shouldContainOnlyAllowedCharacters() {
        // Given
        String allowedCharsRegex = "^[a-zA-Z0-9]+$"; // Регулярний вираз для перевірки алфавіту
        // When
        String result = generator.generate();
        // Then
        assertThat(result).matches(allowedCharsRegex); // Перевіряємо, що немає зайвих символів
    }

    @Test
    void shouldGenerateUniqueStrings() {
        // When: Генеруємо два посилання поспіль
        String first = generator.generate();
        String second = generator.generate();
        // Then: Оскільки SecureRandom рандомний, вони мають бути унікальними
        assertThat(first).isNotEqualTo(second);
    }
}
