package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegistrationRequest {
    @JsonProperty("e_mail")
    private String email;
    private String name;
    private String password;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
