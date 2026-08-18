package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO with information about comment")
public record PostCommentResponse(@Schema(description = "Id of the comment") int id,
                                  @Schema(description = "Date and time of the comment in UTC format") long timestamp,
                                  @Schema(description = "Text of the comment") String text,
                                  BaseUserResponse user) {
}
