package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Set;

@Schema(description = "DTO with information about post got by id")
public record PostDetailsResponse(@Schema(description = "Post id") int id,
                                  @Schema(description = "Date and time of post publication in UTC format") long timestamp,
                                  BaseUserResponse user,
                                  @Schema(description = "Post title") String title,
                                  @Schema(description = "Likes count") long likeCount,
                                  @Schema(description = "Dislikes count") long dislikeCount,
                                  @Schema(description = "Views count") int viewCount,
                                  @Schema(description = "Open or closed. True for open, false for closed") @JsonProperty("active") boolean active,
                                  @Schema(description = "List of comments") List<PostCommentResponse> comments,
                                  @Schema(description = "List of tags") Set<String> tags,
                                  @Schema(description = "Text of post") String text) {
}
