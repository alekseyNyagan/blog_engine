package main.configuration;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class StartupConfig implements CommandLineRunner {
    @Value("${app.upload.folder-path}")
    private String appUploadFolderPath;

    @Override
    public void run(String @NonNull ... args) throws Exception {
        String path = appUploadFolderPath;
        File uploadFolder = new File(path);
        if (!uploadFolder.exists()){
            uploadFolder.mkdirs();
        }
    }
}
