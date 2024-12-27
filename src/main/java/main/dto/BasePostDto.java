package main.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode
@SuperBuilder(toBuilder = true)
@Data
public abstract sealed class BasePostDto permits CurrentPostDto, PostDto {
    @Schema(description = "Post id")
    private int id;
    @Schema(description = "Date and time of post publication in UTC format")
    private long timestamp;
    private BaseUserDto user;
    @Schema(description = "Post title")
    private String title;
    @Schema(description = "Likes count")
    private int likeCount;
    @Schema(description = "Dislikes count")
    private int dislikeCount;
    @Schema(description = "Views count")
    private int viewCount;
}