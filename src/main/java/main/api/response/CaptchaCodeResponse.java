package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Response to the client with captcha image")
@Data
public class CaptchaCodeResponse {
    @Schema(description = "Captcha secret code that should be compared with secret code in db")
    private String secret;
    @Schema(description = "Captcha image")
    private String image;
}
