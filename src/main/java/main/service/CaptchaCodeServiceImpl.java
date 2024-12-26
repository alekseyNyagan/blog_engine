package main.service;

import com.github.cage.Cage;
import main.api.response.CaptchaCodeResponse;
import main.model.CaptchaCode;
import main.repository.CaptchaCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class CaptchaCodeServiceImpl implements CaptchaCodeService {

    @Value("${time.expire.captcha}")
    private int CAPTCHA_EXPIRE_TIME;
    private final CaptchaCodeRepository captchaCodeRepository;

    @Autowired
    public CaptchaCodeServiceImpl(CaptchaCodeRepository captchaCodeRepository) {
        this.captchaCodeRepository = captchaCodeRepository;
    }

    @Override
    public CaptchaCodeResponse getCaptcha() {
        CaptchaCodeResponse captchaCodeResponse = new CaptchaCodeResponse();
        try {
            Cage cage = new Cage();
            String code = cage.getTokenGenerator().next().substring(0, 5);
            String secretCode = cage.getTokenGenerator().next();
            CaptchaCode captchaCode = new CaptchaCode(LocalDateTime.now(), code, secretCode);
            captchaCodeRepository.save(captchaCode);
            BufferedImage bufferedImage = resizeImage(cage.drawImage(code));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            byte[] image = byteArrayOutputStream.toByteArray();
            String encodedString = "data:image/png;base64, " + Base64.getEncoder().encodeToString(image);
            captchaCodeResponse.setImage(encodedString);
            captchaCodeResponse.setSecret(secretCode);
            captchaCodeRepository.deleteAllByTimeBefore(CAPTCHA_EXPIRE_TIME);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return captchaCodeResponse;
    }

    private BufferedImage resizeImage(BufferedImage originalImage) throws IOException {
        BufferedImage resizedImage = new BufferedImage(100, 35, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, 100, 35, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
