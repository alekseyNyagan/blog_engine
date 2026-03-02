package main.service;

import com.github.cage.Cage;
import com.github.cage.token.RandomTokenGenerator;
import main.api.response.CaptchaCodeResponse;
import main.model.CaptchaCode;
import main.repository.CaptchaCodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaptchaCodeServiceTest {

    @Mock
    private CaptchaCodeRepository captchaCodeRepository;
    @Mock
    private ImageService imageService;
    @Mock
    private Cage cage;

    @InjectMocks
    private CaptchaCodeService captchaCodeService;

    @Test
    @DisplayName("getCaptcha should save captcha, return image and secret, and delete expired captchas")
    void getCaptcha_ShouldSaveCaptchaAndReturnResponse() throws IOException {
        // given
        RandomTokenGenerator tokenGenerator = mock(RandomTokenGenerator.class);
        when(tokenGenerator.next())
                .thenReturn("ABCDEFGH")   // первый вызов — код (берётся substring 0..5 = "ABCDE")
                .thenReturn("secret-xyz"); // второй вызов — secretCode
        when(cage.getTokenGenerator()).thenReturn(tokenGenerator);
        when(imageService.drawCaptchaImage(any(), eq("ABCDE"))).thenReturn("data:image/png;base64,abc");

        // when
        CaptchaCodeResponse response = captchaCodeService.getCaptcha();

        // then
        assertThat(response.getSecret()).isEqualTo("secret-xyz");
        assertThat(response.getImage()).isEqualTo("data:image/png;base64,abc");

        // проверяем, что капча сохранена с правильными полями
        ArgumentCaptor<CaptchaCode> captor = ArgumentCaptor.forClass(CaptchaCode.class);
        verify(captchaCodeRepository).save(captor.capture());
        assertThat(captor.getValue().getCode()).isEqualTo("ABCDE");
        assertThat(captor.getValue().getSecretCode()).isEqualTo("secret-xyz");

        // проверяем, что устаревшие капчи удаляются
        verify(captchaCodeRepository).deleteAllByTimeLessThan(any(Instant.class));
    }

    @Test
    @DisplayName("isCaptchaNotValid should return false when secret and code match")
    void isCaptchaNotValid_ShouldReturnFalse_WhenCodeMatches() {
        // given
        CaptchaCode captchaCode = new CaptchaCode(Instant.now(), "12345", "my-secret");
        when(captchaCodeRepository.findCaptchaCodeBySecretCode("my-secret"))
                .thenReturn(Optional.of(captchaCode));

        // when / then
        assertThat(captchaCodeService.isCaptchaNotValid("my-secret", "12345")).isFalse();
    }

    @Test
    @DisplayName("isCaptchaNotValid should return true when code does not match")
    void isCaptchaNotValid_ShouldReturnTrue_WhenCodeMismatch() {
        // given
        CaptchaCode captchaCode = new CaptchaCode(Instant.now(), "12345", "my-secret");
        when(captchaCodeRepository.findCaptchaCodeBySecretCode("my-secret"))
                .thenReturn(Optional.of(captchaCode));

        // when / then
        assertThat(captchaCodeService.isCaptchaNotValid("my-secret", "wrong-code")).isTrue();
    }

    @Test
    @DisplayName("isCaptchaNotValid should return true when secret not found in database")
    void isCaptchaNotValid_ShouldReturnTrue_WhenSecretNotFound() {
        // given
        when(captchaCodeRepository.findCaptchaCodeBySecretCode("unknown")).thenReturn(Optional.empty());

        // when / then
        assertThat(captchaCodeService.isCaptchaNotValid("unknown", "any-code")).isTrue();
    }
}
