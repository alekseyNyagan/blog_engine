package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PostVoteRequest {
    @Schema(description = "Post id for vote")
    @JsonProperty("post_id")
    private int postId;
}
