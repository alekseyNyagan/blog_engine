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
        assertThat(captchaCodeRepository.findCaptchaCodeBySecretCode(captchaCode.getSecretCode()).getSecretCode()).isEqualTo("123456");
    }
}
