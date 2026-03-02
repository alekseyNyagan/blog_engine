package main.repository;

import main.model.CaptchaCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test-containers-flyway")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CaptchaCodeRepositoryIntegrationTest {

    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;

    @Test
    @DisplayName("Should find captcha code by secret code in database")
    void findCaptchaCodeBySecretCodeTest() {
        CaptchaCode captchaCode = new CaptchaCode(Instant.now(), "123", "123456");
        captchaCodeRepository.saveAndFlush(captchaCode);

        assertThat(captchaCodeRepository.findCaptchaCodeBySecretCode("123456"))
                .isPresent()
                .get()
                .extracting(CaptchaCode::getSecretCode)
                .isEqualTo("123456");
    }

    @Test
    @DisplayName("Should delete captcha codes older than threshold, keeping recent ones")
    void deleteAllByTimeLessThanTest() {
        // Капча с временем в прошлом (2 часа назад) — должна быть удалена
        CaptchaCode oldCaptcha = new CaptchaCode(Instant.now().minus(2, ChronoUnit.HOURS), "old", "old-secret");
        // Капча с текущим временем — должна остаться
        CaptchaCode recentCaptcha = new CaptchaCode(Instant.now(), "new", "new-secret");

        captchaCodeRepository.saveAndFlush(oldCaptcha);
        captchaCodeRepository.saveAndFlush(recentCaptcha);

        Instant threshold = Instant.now().minus(1, ChronoUnit.HOURS);
        captchaCodeRepository.deleteAllByTimeLessThan(threshold);

        List<CaptchaCode> remaining = captchaCodeRepository.findAll();
        assertThat(remaining)
                .extracting(CaptchaCode::getSecretCode)
                .doesNotContain("old-secret")
                .contains("new-secret");
    }
}
