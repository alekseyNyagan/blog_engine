package main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@Data
public class UserDto extends BaseUserDto {
    @Schema(description = "Email of the user")
    private String email;
    @Schema(description = "Is user moderator or not")
    @JsonProperty("moderation")
    private boolean moderator;
    @Schema(description = "Count of posts that moderator should moderate")
    private int moderationCount;
    @Schema(description = "Is user can change settings")
    private boolean settings;
}
