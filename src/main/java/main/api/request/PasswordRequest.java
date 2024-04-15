package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordRequest {
    @Schema(description = "Code of password restore")
    private String code;
    @Schema(description = "New password")
    private String password;
    @Schema(description = "Captcha code that user needs to enter")
    private String captcha;
    @Schema(description = "Secret code that need to be compared with the code from the db")
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
