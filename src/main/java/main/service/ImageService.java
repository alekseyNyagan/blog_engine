package main.service;

import com.github.cage.Cage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.api.response.ErrorsResponse;
import main.configuration.UploadProperties;
import main.exception.FileUploadException;
import main.exception.ValidationException;
import main.utils.RandomUtil;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private static final int LENGTH_OF_HASH = 5;
    private static final int CAPTCHA_WIDTH = 100;
    private static final int CAPTCHA_HEIGHT = 35;
    private static final int THUMBNAIL_SIZE = 36;

    private static final String PNG_FILE_EXTENSION = "png";
    private static final String FILE_SIZE_ERROR_MESSAGE = "Размер файла превышает допустимый размер";
    private static final String FILE_EXTENSION_ERROR_MESSAGE = "Неверный формат изображения! Изображение должно быть в формате png или jpg";
    private static final String FILE_UPLOAD_ERROR_MESSAGE = "Не удалось сохранить файл из-за внутренней ошибки сервера.";
    private static final String IMAGE_ERROR_KEY = "image";
    private static final String ENCODED_STRING_PREFIX = "data:image/png;base64,";
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg");

    private final UploadProperties uploadProperties;

    public Object uploadImage(MultipartFile file) throws IOException {
        log.info("Uploading image: {}", file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        try {
            validateImage(file);
            String relativePath = uploadProperties.getRelativePrefix() + RandomUtil.generateRandomHash(LENGTH_OF_HASH) + "." + extension;
            String fullPath = uploadProperties.getFolderPath();

            try (FileOutputStream outputStream = new FileOutputStream(fullPath)) {
                outputStream.write(file.getBytes());
                log.info("Image {} uploaded successfully to {}", file.getOriginalFilename(), fullPath);
                return relativePath;
            }
        } catch (ValidationException e) {
            return new ErrorsResponse(false, e.getErrors());
        }
    }

    public String processAndEncodeImage(MultipartFile file) {
        log.info("Processing and encoding image: {}", file.getOriginalFilename());
        try (InputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
        BufferedImage image = ImageIO.read(inputStream);
        BufferedImage resizedImage = resizeImage(image, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        return convertToBase64PngDataUrl(resizedImage);
        } catch (IOException e) {
            log.warn("Error while processing and encoding image", e);
            throw new FileUploadException(FILE_UPLOAD_ERROR_MESSAGE);
        }
    }

    public boolean isImageSizeValid(MultipartFile file) {
        return file.getSize() <= uploadProperties.getMaxSize();
    }

    public String drawCaptchaImage(Cage cage, String code) {
        BufferedImage resizedImage = resizeImage(cage.drawImage(code), CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        return convertToBase64PngDataUrl(resizedImage);
    }

    private void validateImage(MultipartFile file) {
        Map<String, String> errors = new HashMap<>();
        if (!isImageSizeValid(file)) {
            log.warn("Failed to upload image: size {} exceeds max", file.getSize());
            errors.put(IMAGE_ERROR_KEY, FILE_SIZE_ERROR_MESSAGE);
            throw new ValidationException(errors);
        } else if (!ALLOWED_EXTENSIONS.contains(file.getContentType())) {
            log.warn("Failed to upload image: type {} is not allowed", file.getContentType());
            errors.put(IMAGE_ERROR_KEY, FILE_EXTENSION_ERROR_MESSAGE);
            throw new ValidationException(errors);
        }
    }

    private static @NotNull String convertToBase64PngDataUrl(BufferedImage resizedImage) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(resizedImage, PNG_FILE_EXTENSION, byteArrayOutputStream);
            byte[] image = byteArrayOutputStream.toByteArray();
            return ENCODED_STRING_PREFIX + Base64.getEncoder().encodeToString(image);
        } catch (IOException e) {
            log.warn("Failed to convert image to Base64 String", e);
            throw new FileUploadException(FILE_UPLOAD_ERROR_MESSAGE);
        }
    }

    public BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, width, height, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
