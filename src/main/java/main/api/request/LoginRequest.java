package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(@Schema(description = "User's email") @JsonProperty("e_mail") String email,
                           @Schema(description = "User's password") String password) {
}
