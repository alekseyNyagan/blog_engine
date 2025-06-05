package main.service;

import com.github.cage.Cage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    Object uploadImage(MultipartFile file) throws IOException;
    String processAndEncodeImage(MultipartFile file) throws IOException;
    boolean isImageSizeValid(MultipartFile file);
    String drawCaptchaImage(Cage cage, String code) throws IOException;
}
