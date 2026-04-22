package main.configuration;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class StartupConfig implements CommandLineRunner {
    @Override
    public void run(String @NonNull ... args) throws Exception {
        String path = "./upload/ab/cd/ef/";
        File uploadFolder = new File(path);
        if (!uploadFolder.exists()){
            uploadFolder.mkdirs();
        }
    }
}
