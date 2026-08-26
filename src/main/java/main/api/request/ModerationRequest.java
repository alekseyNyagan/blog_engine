package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record ModerationRequest(@Schema(description = "Post id that should be moderated") @JsonProperty("post_id") int postId,
                                @Schema(description = "Decision of the moderator") String decision) {
}
