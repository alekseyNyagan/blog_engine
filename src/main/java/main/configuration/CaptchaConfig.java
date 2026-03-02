package main.configuration;

import com.github.cage.Cage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaptchaConfig {

    @Bean
    public Cage cage() {
        return new Cage();
    }
}
