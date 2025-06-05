package main.service;

import com.github.cage.Cage;
import main.api.response.ErrorsResponse;
import main.utils.ImageUtil;
import main.utils.RandomUtil;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {

    private static final int MAX_FILE_SIZE = 5_242_880;
    private static final int CAPTCHA_WIDTH = 100;
    private static final int CAPTCHA_HEIGHT = 35;
    private static final String PNG_FILE_EXTENSION = "png";
    private static final String ENCODED_STRING_PREFIX = "data:image/png;base64,";

    @Override
    public String processAndEncodeImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        BufferedImage resizedImage = ImageUtil.resizeImage(image, 36, 36);
        return convertToBase64PngDataUrl(resizedImage);
    }

    @Override
    public boolean isImageSizeValid(MultipartFile file) {
        return file.getSize() <= MAX_FILE_SIZE;
    }

    @Override
    public String drawCaptchaImage(Cage cage, String code) throws IOException {
        BufferedImage resizedImage = ImageUtil.resizeImage(cage.drawImage(code), CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        return convertToBase64PngDataUrl(resizedImage);
    }

    private static @NotNull String convertToBase64PngDataUrl(BufferedImage resizedImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, PNG_FILE_EXTENSION, byteArrayOutputStream);
        byte[] image = byteArrayOutputStream.toByteArray();
        return ENCODED_STRING_PREFIX + Base64.getEncoder().encodeToString(image);
    }
}
