package main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends AbstractDTO {
    @Schema(description = "Id of the user")
    private int id;
    @Schema(description = "Name of the user")
    private String name;
    @Schema(description = "Photo of the user")
    private String photo;
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
