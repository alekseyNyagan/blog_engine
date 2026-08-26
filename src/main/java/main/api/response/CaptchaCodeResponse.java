package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response to the client with captcha image")
public record CaptchaCodeResponse(@Schema(description = "Captcha secret code that should be compared with secret code in db") String secret,
                                  @Schema(description = "Captcha image") String image) {
}
