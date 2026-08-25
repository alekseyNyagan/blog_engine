package main.service;

import com.github.cage.Cage;
import lombok.extern.slf4j.Slf4j;
import main.api.response.CaptchaCodeResponse;
import main.model.CaptchaCode;
import main.repository.CaptchaCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class CaptchaCodeService {

    private static final int CODE_START_INDEX = 0;
    private static final int CODE_END_INDEX = 5;

    @Value("${time.expire.captcha}")
    private int captchaExpireTime;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final ImageService imageService;
    private final Cage cage;

    @Autowired
    public CaptchaCodeService(CaptchaCodeRepository captchaCodeRepository, ImageService imageService, Cage cage) {
        this.captchaCodeRepository = captchaCodeRepository;
        this.imageService = imageService;
        this.cage = cage;
    }

    @Transactional
    public CaptchaCodeResponse getCaptcha() {
        String code = cage.getTokenGenerator().next().substring(CODE_START_INDEX, CODE_END_INDEX);
        String secretCode = cage.getTokenGenerator().next();

        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode(code);
        captchaCode.setSecretCode(secretCode);

        captchaCodeRepository.save(captchaCode);

        String image = imageService.drawCaptchaImage(cage, code);

        Instant threshold = Instant.now().minus(1L, ChronoUnit.HOURS);
        captchaCodeRepository.deleteAllByTimeLessThan(threshold);
        return new CaptchaCodeResponse(secretCode, image);
    }

    public boolean isCaptchaNotValid(String secret, String value) {
        return captchaCodeRepository.findCaptchaCodeBySecretCode(secret)
                .map(captcha -> !captcha.getCode().equals(value))
                .orElse(true);
    }
}
