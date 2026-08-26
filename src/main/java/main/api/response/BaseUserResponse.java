package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO with partial user information")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseUserResponse(@Schema(description = "User id") int id,
                               @Schema(description = "User name") String name,
                               @Schema(description = "User photo") String photo) {
}
