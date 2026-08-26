package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record PasswordRequest(@Schema(description = "Code of password restore") String code,
                              @Schema(description = "New password") String password,
                              @Schema(description = "Captcha code that user needs to enter") String captcha,
                              @Schema(description = "Secret code that need to be compared with the code from the db") @JsonProperty("captcha_secret") String captchaSecret) {
}
