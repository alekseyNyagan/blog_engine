package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Schema(description = "Response to the client with info about blog")
@ConfigurationProperties(prefix = "blog")
public record InitResponse(String title, String subtitle, String phone, String email, String copyright, String copyrightFrom) {
}
