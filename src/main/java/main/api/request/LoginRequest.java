package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(description = "User's email")
    @JsonProperty("e_mail")
    private String email;
    @Schema(description = "User's password")
    private String password;
}
