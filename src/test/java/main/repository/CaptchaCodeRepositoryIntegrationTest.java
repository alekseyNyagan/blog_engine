package main.repository;

import main.model.CaptchaCode;
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
        CaptchaCode captchaCode = new CaptchaCode(LocalDateTime.now(), "123", "123456");
        captchaCodeRepository.saveAndFlush(captchaCode);
        assertThat(captchaCodeRepository.findCaptchaCodeBySecretCode(captchaCode.getSecretCode()).getSecretCode()).isEqualTo("123456");
    }

    @Test
    @DisplayName("Should delete all captcha codes after expiration time from database")
    void deleteAllByTimeBeforeTest() {
        CaptchaCode captchaCode = new CaptchaCode(LocalDateTime.now(), "123", "123456");
        CaptchaCode captchaCode2 = new CaptchaCode(LocalDateTime.now().minusHours(2), "456", "456789");
        captchaCodeRepository.saveAll(List.of(captchaCode, captchaCode2));
        captchaCodeRepository.deleteAllByTimeBefore(1);
        assertThat(captchaCodeRepository.findCaptchaCodeBySecretCode(captchaCode2.getSecretCode())).isNull();
        assertThat(captchaCodeRepository.findCaptchaCodeBySecretCode(captchaCode.getSecretCode()).getSecretCode()).isEqualTo("123456");
    }
}
