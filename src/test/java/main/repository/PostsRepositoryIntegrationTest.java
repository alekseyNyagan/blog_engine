package main.repository;

import main.model.User;
import main.model.enums.ModerationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
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

    @Autowired
    UsersRepository usersRepository;

    @Test
    @DisplayName("Should return posts count by moderation status")
    void shouldReturnPostsCountByModerationStatus() {
        assertThat(postsRepository.countPostsByModerationStatus(ModerationStatus.NEW)).isEqualTo(1);
        assertThat(postsRepository.countPostsByModerationStatus(ModerationStatus.DECLINED)).isEqualTo(0);
        assertThat(postsRepository.countPostsByModerationStatus(ModerationStatus.ACCEPTED)).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return posts count where is active equals 1 and moderation status equals ACCEPTED")
    void shouldReturnPostsCountByIsActiveAndModerationStatus() {
        assertThat(postsRepository.countPostsByIsActiveAndModerationStatus()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return list of years with created posts")
    void shouldReturnListOfYearsWithCreatedPosts() {
        assertArrayEquals(postsRepository.findYearsWithCreatedPosts().toArray(), List.of(2024).toArray());
    }

    @Test
    @DisplayName("Should return list of calenderDTO by year")
    void shouldReturnCountPostsByYear() {
        assertThat(postsRepository.countPostsByYear(2024).size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return count of posts with text")
    void shouldReturnCountPostsByIsActiveAndModerationStatusAndTextLike() {
        assertThat(postsRepository.countPostsByIsActiveAndModerationStatusAndTextLike("Cooking")).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return count of posts with date")
    void shouldReturnCountPostsByIsActiveAndModerationStatusAndTime() {
        LocalDateTime dayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        assertThat(postsRepository.countPostsByIsActiveAndModerationStatusAndTime(dayStart, dayEnd)).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return count of posts with tag name")
    void shouldReturnCountPostsByTagName() {
        assertThat(postsRepository.countPostsByTagName("Food")).isEqualTo(1);
    }

    @Test
    @DisplayName("Should increment view count")
    void shouldIncrementViewCount() {
        postsRepository.incrementViewCount(1);
        assertThat(postsRepository.findById(1).get().getViewCount()).isEqualTo(101);
    }

    @Test
    @DisplayName("Should return count of posts by user")
    void shouldReturnCountPostsByUserAndIsActive() {
        User user = usersRepository.findById(1).get();
        assertThat(postsRepository.countPostsByUserAndIsActive(user, (byte) 1)).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return count of posts by user and moderation status")
    void shouldReturnCountPostsByUserAndIsActiveAndModerationStatus() {
        User user = usersRepository.findById(1).get();
        assertThat(postsRepository.countPostsByUserAndIsActiveAndModerationStatus(user, (byte) 1, ModerationStatus.ACCEPTED))
                .isEqualTo(1);
        assertThat(postsRepository.countPostsByUserAndIsActiveAndModerationStatus(user, (byte) 1, ModerationStatus.DECLINED))
                .isEqualTo(0);
        assertThat(postsRepository.countPostsByUserAndIsActiveAndModerationStatus(user, (byte) 1, ModerationStatus.NEW))
                .isEqualTo(0);
    }

    @Test
    @DisplayName("Should update moderation status")
    void shouldUpdateModerationStatus() {
        postsRepository.updateModerationStatus(ModerationStatus.ACCEPTED, 1, 2);
        assertThat(postsRepository.findById(2).get().getModerationStatus()).isEqualTo(ModerationStatus.ACCEPTED);
    }
}