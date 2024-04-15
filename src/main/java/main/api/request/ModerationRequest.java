package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ModerationRequest {
    @Schema(description = "Post id that should be moderated")
    @JsonProperty("post_id")
    private int postId;
    @Schema(description = "Decision of the moderator")
    private String decision;
}
