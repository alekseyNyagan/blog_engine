package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(@Schema(description = "User id") int id,
                           @Schema(description = "User name") String name,
                           @Schema(description = "User photo") String photo,
                           @Schema(description = "Email of the user") String email,
                           @Schema(description = "Is user moderator or not") @JsonProperty("moderation") boolean moderator,
                           @Schema(description = "Count of posts that moderator should moderate") int moderationCount,
                           @Schema(description = "Is user can change settings") boolean settings) {
}
