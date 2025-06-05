package main.service;

import com.github.cage.Cage;
import main.api.response.CaptchaCodeResponse;
import main.model.CaptchaCode;
import main.repository.CaptchaCodeRepository;
import main.utils.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class CaptchaCodeServiceImpl implements CaptchaCodeService {

    private static final int CAPTCHA_WIDTH = 100;
    private static final int CAPTCHA_HEIGHT = 35;
    private static final int CODE_START_INDEX = 0;
    private static final int CODE_END_INDEX = 5;
    private static final String PNG_FILE_EXTENSION = "png";
    private static final String ENCODED_STRING_PREFIX = "data:image/png;base64, ";
    @Value("${time.expire.captcha}")
    private int captchaExpireTime;
    private final CaptchaCodeRepository captchaCodeRepository;

    @Autowired
    public CaptchaCodeServiceImpl(CaptchaCodeRepository captchaCodeRepository) {
        this.captchaCodeRepository = captchaCodeRepository;
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
            captchaCodeResponse.setImage(drawImage(cage, code));
            captchaCodeResponse.setSecret(secretCode);
            captchaCodeRepository.deleteAllByTimeBefore(captchaExpireTime);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return captchaCodeResponse;
    }

    @Override
    public boolean isCaptchaNotValid(String secret, String value) {
        return !captchaCodeRepository.findCaptchaCodeBySecretCode(secret).getCode().equals(value);
    }

    private static String drawImage(Cage cage, String code) throws IOException {
        BufferedImage bufferedImage = ImageUtil.resizeImage(cage.drawImage(code), CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, PNG_FILE_EXTENSION, byteArrayOutputStream);
        byte[] image = byteArrayOutputStream.toByteArray();
        return ENCODED_STRING_PREFIX + Base64.getEncoder().encodeToString(image);
    }
}
