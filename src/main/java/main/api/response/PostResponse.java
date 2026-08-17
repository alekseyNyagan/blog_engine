package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import main.dto.BaseUserDto;

@Schema(description = "DTO with post data")
public record PostResponse(@Schema(description = "Post id") int id,
                           @Schema(description = "Date and time of post publication in UTC format") long timestamp,
                           BaseUserDto user,
                           @Schema(description = "Post title") String title,
                           @Schema(description = "Likes count") long likeCount,
                           @Schema(description = "Dislikes count") long dislikeCount,
                           @Schema(description = "Views count") int viewCount,
                           @Schema(description = "Small part of post content") String announce,
                           @Schema(description = "Comments count") long commentCount

) {
}
