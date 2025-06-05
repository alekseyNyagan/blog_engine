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

import java.io.IOException;

@Service
@Slf4j
public class CaptchaCodeServiceImpl implements CaptchaCodeService {

    private static final int CODE_START_INDEX = 0;
    private static final int CODE_END_INDEX = 5;

    @Value("${time.expire.captcha}")
    private int captchaExpireTime;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final ImageService imageService;

    @Autowired
    public CaptchaCodeServiceImpl(CaptchaCodeRepository captchaCodeRepository, ImageService imageService) {
        this.captchaCodeRepository = captchaCodeRepository;
        this.imageService = imageService;
    }

    @Transactional
    @Override
    public CaptchaCodeResponse getCaptcha() {
        CaptchaCodeResponse captchaCodeResponse = new CaptchaCodeResponse();
        try {
            Cage cage = new Cage();
            String code = cage.getTokenGenerator().next().substring(CODE_START_INDEX, CODE_END_INDEX);
            String secretCode = cage.getTokenGenerator().next();
            CaptchaCode captchaCode = new CaptchaCode();
            captchaCode.setCode(code);
            captchaCode.setSecretCode(secretCode);
            captchaCodeRepository.save(captchaCode);
            captchaCodeResponse.setImage(imageService.drawCaptchaImage(cage, code));
            captchaCodeResponse.setSecret(secretCode);
            captchaCodeRepository.deleteAllByTimeBefore(captchaExpireTime);
        } catch (IOException exception) {
            log.error("Ошибка генерации изображения капчи");
        }
        return captchaCodeResponse;
    }

    @Override
    public boolean isCaptchaNotValid(String secret, String value) {
        return !captchaCodeRepository.findCaptchaCodeBySecretCode(secret).getCode().equals(value);
    }
}
