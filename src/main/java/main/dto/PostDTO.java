package main.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "DTO with post data")
@EqualsAndHashCode(callSuper = true)
@Data
public class PostDTO extends AbstractDTO {
    @Schema(description = "Post id")
    private int id;
    @Schema(description = "Date and time of post publication in UTC format")
    private long timestamp;
    private IncompleteUserDTO user;
    @Schema(description = "Post title")
    private String title;
    @Schema(description = "Small part of post content")
    private String announce;
    @Schema(description = "Likes count")
    private int likeCount;
    @Schema(description = "Comments count")
    private int commentCount;
    @Schema(description = "Dislikes count")
    private int dislikeCount;
    @Schema(description = "Views count")
    private int viewCount;
}
