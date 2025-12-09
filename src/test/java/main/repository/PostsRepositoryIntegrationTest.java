package main.repository;

import main.model.enums.ModerationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;


@Testcontainers
@DataJpaTest
@ActiveProfiles("test-containers-flyway")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostsRepositoryIntegrationTest {

    @Autowired
    PostsRepository postsRepository;

    @Test
    @DisplayName("Should return posts count by moderation status")
    void shouldReturnPostsCountByModerationStatus() {
        assertThat(postsRepository.countPostsByModerationStatus(ModerationStatus.NEW)).isEqualTo(1);
        assertThat(postsRepository.countPostsByModerationStatus(ModerationStatus.DECLINED)).isZero();
        assertThat(postsRepository.countPostsByModerationStatus(ModerationStatus.ACCEPTED)).isEqualTo(12);
    }

    @Test
    @DisplayName("Should return list of years with created posts")
    void shouldReturnListOfYearsWithCreatedPosts() {
        assertArrayEquals(postsRepository.findYearsWithCreatedPosts().toArray(), List.of(2024).toArray());
    }

    @Test
    @DisplayName("Should return list of calenderDTO by year")
    void shouldReturnCountPostsByYear() {
        assertThat(postsRepository.countPostsByYear(2024)).hasSize(2);
    }
}