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
public class ImageService {

    private static final int MAX_FILE_SIZE = 5_242_880;
    private static final int LENGTH_OF_HASH = 5;
    private static final int CAPTCHA_WIDTH = 100;
    private static final int CAPTCHA_HEIGHT = 35;
    private static final String PNG_FILE_EXTENSION = "png";
    private static final String JPG_FILE_EXTENSION = "jpg";
    private static final String UPLOAD_FOLDER_PATH = "/upload/ab/cd/ef/";
    private static final String FILE_SIZE_ERROR_MESSAGE = "Размер файла превышает допустимый размер";
    private static final String FILE_EXTENSION_ERROR_MESSAGE = "Неверный формат изображения! Изображение должно быть в формате png или jpg";
    private static final String IMAGE_ERROR_KEY = "image";
    private static final String ENCODED_STRING_PREFIX = "data:image/png;base64,";

    public Object uploadImage(MultipartFile file) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (extension != null && isImageSizeValid(file) &&
                (extension.equalsIgnoreCase(PNG_FILE_EXTENSION) || extension.equalsIgnoreCase(JPG_FILE_EXTENSION))) {
            String hash = RandomUtil.generateRandomHash(LENGTH_OF_HASH);
            String relativePath = UPLOAD_FOLDER_PATH + hash + "." + extension;
            String fullPath = "." + relativePath;

            try (FileOutputStream outputStream = new FileOutputStream(fullPath)) {
                outputStream.write(file.getBytes());
                return relativePath;
            }
        } else {
            return getErrorsResponse(file);
        }
    }

    public String processAndEncodeImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        BufferedImage resizedImage = ImageUtil.resizeImage(image, 36, 36);
        return convertToBase64PngDataUrl(resizedImage);
    }

    public boolean isImageSizeValid(MultipartFile file) {
        return file.getSize() <= MAX_FILE_SIZE;
    }

    public String drawCaptchaImage(Cage cage, String code) throws IOException {
        BufferedImage resizedImage = ImageUtil.resizeImage(cage.drawImage(code), CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        return convertToBase64PngDataUrl(resizedImage);
    }

    private static ErrorsResponse getErrorsResponse(MultipartFile multipartFile) {
        Map<String, String> errors = new HashMap<>();
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            errors.put(IMAGE_ERROR_KEY, FILE_SIZE_ERROR_MESSAGE);
        } else {
            errors.put(IMAGE_ERROR_KEY, FILE_EXTENSION_ERROR_MESSAGE);
        }
        return new ErrorsResponse(false, errors);
    }

    private static @NotNull String convertToBase64PngDataUrl(BufferedImage resizedImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, PNG_FILE_EXTENSION, byteArrayOutputStream);
        byte[] image = byteArrayOutputStream.toByteArray();
        return ENCODED_STRING_PREFIX + Base64.getEncoder().encodeToString(image);
    }
}
