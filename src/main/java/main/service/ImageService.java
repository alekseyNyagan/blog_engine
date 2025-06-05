package main.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    String processAndEncodeImage(MultipartFile file) throws IOException;
    boolean isImageSizeValid(MultipartFile file);
}
