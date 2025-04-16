package main.repository;

import main.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test-containers-flyway")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersRepositoryIntegrationTest {

    @Autowired
    private UsersRepository usersRepository;

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        assertThat(usersRepository.findUserByEmail("admin@admin.ru")).isPresent();
    }

    @Test
    @DisplayName("Should find user by code")
    @Tag("skipBeforeEach")
    void shouldFindUserByCode() {
        User user2 = new User(
                (byte) 0
                , LocalDateTime.of(2022, 9, 18, 22, 1, 33).toInstant(ZoneOffset.UTC)
                , "Max"
                , "max@example.com"
                , "$2a$12$7GZ5zptWYUt364zNLKaT2ut3XAU29J3hmxM3avVs/bKPjN0fjYECy"
                , "123456"
                , null);
        usersRepository.saveAndFlush(user2);
        assertThat(usersRepository.findUserByCode("123456")).isPresent();
    }
}