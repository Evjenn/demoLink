package com.example.demolink.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demolink.model.entity.LinkEntity;
import com.example.demolink.model.entity.Role;
import com.example.demolink.model.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@DataJpaTest
@Testcontainers
class LinkRepositoryTest {

    @BeforeAll
    static void setup() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16")
                    .withDatabaseName("test-db")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(
            DynamicPropertyRegistry registry) {

        registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                postgres::getUsername
        );

        registry.add(
                "spring.datasource.password",
                postgres::getPassword
        );
    }

    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveLink() {

        UserEntity user = new UserEntity();
        user.setUsername("john");
        user.setPassword("1234");
        user.setRole(Role.USER);
        user = userRepository.save(user);

        LinkEntity link = new LinkEntity();
        link.setShortLink("abc");
        link.setOriginalLink("https://github.com");
        link.setExpiresAt(LocalDateTime.now().plusDays(10));
        link.setUser(user);
        link.setActive(true);
        LinkEntity saved = linkRepository.save(link);

        assertNotNull(saved.getId());
    }
}
