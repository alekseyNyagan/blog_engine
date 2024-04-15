package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegistrationRequest {
    @Schema(description = "The email of the new user")
    @JsonProperty("e_mail")
    private String email;
    @Schema(description = "The name of the new user")
    private String name;
    @Schema(description = "The password of the new user")
    private String password;
    @Schema(description = "Captcha that user needs to enter")
    private String captcha;
    @Schema(description = "secret code that need to compare with code in db")
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
